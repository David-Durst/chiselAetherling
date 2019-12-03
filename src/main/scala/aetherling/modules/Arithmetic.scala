package aetherling.modules

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._

/**
  * Add two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Add(t: ST_Int) extends MultiIOModule with BinaryInterface with ValidInterface {
  override val in0 = IO(Input(t.chiselRepr()))
  override val in1 = IO(Input(t.chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))
  out := in0 + in1
  valid_down := valid_up
}

/**
  * Abs of an Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class Abs(t: ST_Int) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val in = IO(Input(t.chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))
  when(in.asSInt() < 0.S) { out := 0.U - in }.otherwise( out := in )
  valid_down := valid_up
}
