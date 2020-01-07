package aetherling.modules

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._

/**
  * Add two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Add(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val in = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))
  out := in.t0b.asUInt() + in.t1b.asUInt()
  valid_down := valid_up
}

/**
  * Add two Int atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class AddNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val in = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))
  out := in.t0b.asUInt() + in.t1b.asUInt()
}

/**
  * Abs of an Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class Abs(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val in = IO(Input(t.chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))
  when(in.asSInt() < 0.S) { out := 0.U - in }.otherwise( out := in )
  valid_down := valid_up
}

/**
  * Abs of an Int atom with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class AbsNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val in = IO(Input(t.chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))
  when(in.asSInt() < 0.S) { out := 0.U - in }.otherwise( out := in )
}

/**
  * Not of an Bit atom
  */
class Not() extends MultiIOModule with UnaryInterface with ValidInterface {
  override val in = IO(Input(STBit().chiselRepr()))
  override val out = IO(Output(STBit().chiselRepr()))
  out := ~in
  valid_down := valid_up
}

/**
  * Not of an Bit atom with no valid interface
  */
class NotNoValid() extends MultiIOModule with UnaryInterface {
  override val in = IO(Input(STBit().chiselRepr()))
  override val out = IO(Output(STBit().chiselRepr()))
  out := ~in
}
