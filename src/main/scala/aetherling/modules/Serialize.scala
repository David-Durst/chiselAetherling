package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.{Counter, MuxLookup}

class Serialize(n: Int, i: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Output(TSeq(1, n + i - 1, SSeq_Tuple(n, elem_t)).chiselRepr()))
  val O = IO(Output(TSeq(n, i, elem_t).chiselRepr()))

  val elem_counter = Module(new NestedCountersWithNumValid(elem_t, false))
  elem_counter.CE := valid_up
  val (bank_counter_value, _) = Counter(valid_up && elem_counter.last, n)

  val mux_input_wire = Wire(Vec(n, elem_t.chiselRepr()))
  mux_input_wire(0) := I.asInstanceOf[Vec[Data]](0)

  if (n > 1) {
    if (elem_t.validClocks() > 1) {
      val rams = for (_ <- 1 to n-1) yield SyncReadMem(elem_t.validClocks(), elem_t.chiselRepr())

      for (i <- 1 to (n-1)) {
        when(bank_counter_value === 0.U && valid_up) { rams(i).write(elem_counter.cur_valid, I.asInstanceOf[Vec[Data]](i)) }
            .otherwise { mux_input_wire(i-1) := rams(i).read(elem_counter.cur_valid, valid_up) }
      }
    }
    else {
      val regs = for (_ <- 0 to n-2) yield Reg(elem_t.chiselRepr())
      val (read_bank_counter_value, _) = Counter(valid_up, n)

      for (i <- 1 to n-1) {
        when (read_bank_counter_value === 0.U && valid_up) { regs(i) := I.asInstanceOf[Vec[Data]](i)}
        mux_input_wire(i-1) := regs(i)
      }
    }
  }

  O := RegNext(MuxLookup(bank_counter_value, mux_input_wire(0), for (i <- 0 to n-1) yield i.U -> mux_input_wire(i)))
  valid_down := RegNext(valid_up, false.B)
}
