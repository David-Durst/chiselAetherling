package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.{Counter, MuxLookup}

class Serialize(n: Int, i: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Input(TSeq(1, n + i - 1, SSeq_Tuple(n, elem_t)).chiselRepr()))
  val O = IO(Output(TSeq(n, i, elem_t).chiselRepr()))

  val elem_counter = Module(new NestedCountersWithNumValid(elem_t, false))
  //printf(p"elem_counter cur_valid: ${elem_counter.cur_valid}\n")
  elem_counter.CE := valid_up
  val (bank_counter_value, _) = Counter(valid_up && elem_counter.last, n)
  //printf(p"bank_counter_value: ${bank_counter_value}\n")

  val mux_input_wire = Wire(Vec(n, elem_t.chiselRepr()))
  mux_input_wire(0) := I.asInstanceOf[Vec[Data]](0)

  if (n > 1) {
    if (elem_t.time() > 1) {
      val rams = for (_ <- 0 to n-2) yield SyncReadMem(elem_t.validClocks(), elem_t.chiselRepr())

      for (j <- 0 to (n-2)) {
        when(bank_counter_value === 0.U && valid_up) {
          rams(j).write(elem_counter.cur_valid, I.asInstanceOf[Vec[Data]](j+1))
          mux_input_wire(j+1) := DontCare
        }
        val read_addr = Wire(UInt())
        when(elem_counter.cur_valid === (elem_t.validClocks() - 1).U) { read_addr := 0.U }
            .otherwise(read_addr := elem_counter.cur_valid + 1.U)
        mux_input_wire(j+1) := rams(j).read(read_addr, valid_up)
      }
    }
    else {
      val regs = for (_ <- 0 to n-2) yield Reg(elem_t.chiselRepr())
      val (read_bank_counter_value, _) = Counter(valid_up, n)

      for (j <- 0 to n-2) {
        when (read_bank_counter_value === 0.U && valid_up) { regs(j) := I.asInstanceOf[Vec[Data]](j+1)}
        mux_input_wire(j+1) := regs(j)
      }
    }
  }
  /*
  for (j <- 0 to (n-1)) {
    printf(p"mux wire $j: ${mux_input_wire(j)}\n")
  }
   */

  O := RegNext(MuxLookup(bank_counter_value, mux_input_wire(0), for (j <- 0 to n-1) yield j.U -> mux_input_wire(j)))
  valid_down := RegNext(valid_up, false.B)
}
