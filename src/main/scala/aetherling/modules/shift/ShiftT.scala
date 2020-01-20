package aetherling.modules.shift

import aetherling.modules.helpers._
import aetherling.modules._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

/**
  * Shifts the elemnts in (TSeq n i elem_t) by shift amount to the right.
  * @param n The length of the TSeq
  * @param i The invalids of the TSeq
  * @param shift_amount The amount to shift by
  * @param elem_t The element type t
  */
class ShiftT(n: Int, i: Int, shift_amount: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Input(TSeq(n, i, elem_t).chiselRepr()))
  val O = IO(Output(TSeq(n, i, elem_t).chiselRepr()))


  if (elem_t.validClocks() == 1 && shift_amount == 1) {
    O := (0 to elem_t.time() - 1).foldLeft(I){ case (next_to_shift, _) => RegNext(next_to_shift)}
  }
  else {
    // write and read from same location offset by 1 clock cycle
    // must offset by 1 in order to not read and write same address
    // know that have at least 2 places to write to as at least 2 data elements
    // or shifting by at least 2

    // will write on first iteration, write and read on later iterations
    // output for first iteration is undefined, so ok to read anything

    // it's fine that this doesn't account for invalid clocks.
    // after invalid clocks, the next iteration will start from
    // an index that is possibly not 0. That doesn't matter
    // as will just loop around
    val value_store = Module(new RAM_ST(elem_t, shift_amount))

    val next_ram_addr = Module(new NestedCounters(elem_t, valid_down_when_ce_disabled = false))
    next_ram_addr.CE := valid_up
    val (ram_write_addr_value, _) = Counter(valid_up && next_ram_addr.last, shift_amount)

    value_store.WADDR := ram_write_addr_value
    when (ram_write_addr_value === (shift_amount-1).U) { value_store.RADDR := 0.U }
      .otherwise { value_store.RADDR := ram_write_addr_value + 1.U }
    value_store.WE := valid_up
    value_store.RE := valid_up
    value_store.WDATA := I
    O := value_store.RDATA
  }


  valid_down := valid_up
}
