package aetherling.modules.shift

import aetherling.modules.helpers._
import aetherling.modules._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

/**
  * Shifts the elements in (TSeq no io (TSeq ni ii (... elem_t)) by shift amount to the right.
  * @param no The length of the outer TSeq
  * @param nis The lengths of the inner TSeq
  * @param io The invalid of the outer TSeq
  * @param iis The invalids of the inner TSeq
  * @param shift_amount The amount to shift by
  * @param elem_t The element type t
  */
class ShiftTN(no: Int, nis: IndexedSeq[Int], io: Int, iis: IndexedSeq[Int],
              shift_amount: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  require(shift_amount >= 0, "shift_amount must be non-negative")
  var t = elem_t
  for (i <- nis.length - 1 to 0 by -1) {
    t = TSeq(nis(i), iis(i), t)
  }
  val I = IO(Input(TSeq(no, io, t).chiselRepr()))
  val O = IO(Output(TSeq(no, io, t).chiselRepr()))

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

    // it's fine that this doesn't account for outer invalid clocks.
    // after invalid clocks, the next iteration will start from
    // an index that is possibly not 0. That doesn't matter
    // as will just loop around and not keeping data between outer TSeq iterations
    val value_store = Module(new RAM_ST(elem_t, shift_amount))

    val next_ram_addr = Module(new NestedCounters(elem_t, valid_down_when_ce_disabled = false))
    next_ram_addr.CE := valid_up

    // this handles invalid clocks of inner TSeq
    // it matters that handle inner invalid clocks because we preserve
    // data across multiple inner TSeqs so need to save what was going to be
    // emitted until next valid. Draw ShiftT of two TSeq 2 1 Int vs ShiftTT of one TSeq 2 0 (TSeq 2 1 Int)
    // to see and example
    var inner_valid_t = elem_t
    for (i <- nis.length - 1 to 0 by -1) {
      inner_valid_t = TSeq(nis(i), iis(i), inner_valid_t)
    }
    val inner_valid = Module(new NestedCounters(inner_valid_t, valid_down_when_ce_disabled = true))
    inner_valid.CE := valid_up && next_ram_addr.last
    val (ram_write_addr_value, _) = Counter(valid_up && next_ram_addr.last && inner_valid.valid, shift_amount)

    value_store.WADDR := ram_write_addr_value
    when (ram_write_addr_value === (shift_amount-1).U) { value_store.RADDR := 0.U }
      .otherwise { value_store.RADDR := ram_write_addr_value + 1.U }
    value_store.WE := valid_up && inner_valid.valid
    value_store.RE := valid_up && inner_valid.valid
    value_store.WDATA := I
    O := value_store.RDATA
  }


  valid_down := valid_up
}
