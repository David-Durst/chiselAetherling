package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._

class PartitionS(no: Int, ni: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Input(SSeq(no * ni, elem_t).chiselRepr()))
  val O = IO(Output(SSeq(no, SSeq(ni, elem_t)).chiselRepr()))

  for (i <- 0 to no - 1) {
    for (j <- 0 to ni - 1) {
      O(i).asInstanceOf[Vec[Data]](j) := I(i*ni + j)
    }
  }
  valid_down := valid_up
}

class UnpartitionS(no: Int, ni: Int, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Input(SSeq(no, SSeq(ni, elem_t)).chiselRepr()))
  val O = IO(Output(SSeq(no * ni, elem_t).chiselRepr()))

  for (i <- 0 to no - 1) {
    for (j <- 0 to ni - 1) {
      O(i*ni + j) := I(i).asInstanceOf[Vec[Data]](j)
    }
  }
  valid_down := valid_up
}
