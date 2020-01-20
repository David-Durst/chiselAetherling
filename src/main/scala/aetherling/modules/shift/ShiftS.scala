package aetherling.modules.shift

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

/**
  * Shifts the elements in (SSeq n elem_t) by shift amount to the right.
  * @param n The length of the SSeq
  * @param shift_amount The amount to shift by
  * @param elem_t The element type t
  */
class ShiftS(n: Int, shift_amount: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Input(SSeq(n, elem_t).chiselRepr()))
  val O = IO(Output(SSeq(n, elem_t).chiselRepr()))

  for (i <- 0 to n-1) {
    O((i + shift_amount) % n) := I(i)
  }

  valid_down := valid_up
}
