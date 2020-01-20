package aetherling.modules.shift

import aetherling.modules.helpers._
import aetherling.modules._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter
import scala.collection.mutable.MutableList
import java.lang.Math.floorMod

/**
  * Shifts the elements in (TSeq no io (SSeq ni elem_t)) by shift amount to the right.
  * @param no The length of the TSeq
  * @param io The invalid of the TSeq
  * @param ni The lengths of the SSeq
  * @param shift_amount The amount to shift by
  * @param elem_t The element type t
  */
class ShiftTS(no: Int, io: Int, ni: Int, shift_amount: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {

  val I = IO(Input(TSeq(no, io, SSeq(ni, elem_t)).chiselRepr()))
  val O = IO(Output(TSeq(no, io, SSeq(ni, elem_t)).chiselRepr()))

  for (i <- 0 to ni-1) {
    val shift_amount_t = (ni - i + shift_amount - 1) / ni
    if (shift_amount_t == 0) {
      O.asInstanceOf[Vec[Data]](i) := I.asInstanceOf[Vec[Data]](floorMod(i - shift_amount, ni))
    }
    else {
      val shift_one_lane = Module(new ShiftT(no, io, shift_amount_t, elem_t))
      shift_one_lane.I := I.asInstanceOf[Vec[Data]](floorMod(i - shift_amount, ni))
      O.asInstanceOf[Vec[Data]](i) := shift_one_lane.O
      shift_one_lane.valid_up := valid_up
    }
  }

  valid_down := valid_up
}
