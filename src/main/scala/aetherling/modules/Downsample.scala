package aetherling.modules
import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

class DownS(n: Int, idx: Int, elem_t: STTypeDefinition) extends MultiIOModule  with UnaryInterface with ValidInterface {
  val I = IO(Input(SSeq(n, elem_t).chiselRepr()))
  val O = IO(Output(SSeq(1, elem_t).chiselRepr()))

  O := I(idx)
  valid_down := valid_up
}

class DownT(n: Int, i: Int, idx: Int, elem_t: STTypeDefinition) extends MultiIOModule  with UnaryInterface with ValidInterface {
  val I = IO(Input(TSeq(n, i, elem_t).chiselRepr()))
  val O = IO(Output(TSeq(1, n + i - 1, elem_t).chiselRepr()))
  O := I

  if (idx == 0) {
    valid_down := valid_up
  }
  else {
    val initialDelayCounter = Module (new InitialDelayCounter(idx * elem_t.time()))
    initialDelayCounter.valid_up := valid_up
    valid_down := initialDelayCounter.valid_down
  }
}

