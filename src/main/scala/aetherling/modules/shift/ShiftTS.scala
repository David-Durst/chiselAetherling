package aetherling.modules.shift

import aetherling.modules.helpers._
import aetherling.modules._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter
import scala.collection.mutable.MutableList
import java.lang.Math.floorMod
import collection.mutable.Map

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

  val shift_amounts = for (i <- 0 to ni-1) yield (ni - i + shift_amount - 1) / ni
  // don't need a Shift_T to shift by 0, so just drop those
  val shift_amounts_count = shift_amounts.filter({x => x != 0}).groupBy({x => x})
    .map{case (shift_amount, shift_amount_xs) => (shift_amount, shift_amount_xs.size)}
  val shifts = shift_amounts_count.map{case (shift_amount, num_shifted) =>
    (shift_amount, Module(new ShiftT(no, io, shift_amount, SSeq(num_shifted, elem_t))))}
  //  .toSet.map({shift_amount => if (shift_amount == 0) null else Module(new ShiftT(no, io, shift_amount, elem_t)) })
  //println(s"For ShiftTS ${ShiftTS.num_shift_ts_made} the shift_amounts are ${shift_amounts}")
  //ShiftTS.num_shift_ts_made += 1
  val cur_lanes_for_shift_amounts = scala.collection.mutable.Map(
    shift_amounts_count.map{case (shift_amount, _) => (shift_amount, 0)}.toSeq : _*
  )

  for (i <- 0 to ni-1) {
    val shift_amount_t = (ni - i + shift_amount - 1) / ni
    if (shift_amount_t == 0) {
      O.asInstanceOf[Vec[Data]](i) := I.asInstanceOf[Vec[Data]](floorMod(i - shift_amount, ni))
    }
    else {
      //val shift_one_lane = Module(new ShiftT(no, io, shift_amount_t, elem_t))
      val cur_shifts = shifts.get(shift_amount_t).get
      val cur_shift_lane = cur_lanes_for_shift_amounts.getOrElse(shift_amount_t, 0)
      val shift_one_lane_in = cur_shifts.I.asInstanceOf[Vec[Data]]
        .getElements(cur_shift_lane)
      val shift_one_lane_out = cur_shifts.O.asInstanceOf[Vec[Data]]
        .getElements(cur_shift_lane)
      cur_lanes_for_shift_amounts.update(shift_amount_t, cur_shift_lane + 1)
      shift_one_lane_in := I.asInstanceOf[Vec[Data]](floorMod(i - shift_amount, ni))
      O.asInstanceOf[Vec[Data]](i) := shift_one_lane_out
      if (cur_shift_lane == 0) {
        cur_shifts.valid_up := valid_up
      }
    }
  }

  valid_down := valid_up
}

object ShiftTS {
  var num_shift_ts_made = 0
}