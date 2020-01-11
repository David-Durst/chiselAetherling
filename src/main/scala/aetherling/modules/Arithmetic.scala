package aetherling.modules

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._

/**
  * Add two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Add(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() + I.t1b.asUInt()
  valid_down := valid_up
}

/**
  * Add two Int atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class AddNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() + I.t1b.asUInt()
}

/**
  * Abs of an Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class Abs(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(t.chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  val out_reg = Reg(t.chiselRepr())
  when(I.asSInt() < 0.S) { out_reg := 0.U - I }.otherwise( out_reg := I )
  O := out_reg
  valid_down := RegNext(valid_up)
}

/**
  * Abs of an Int atom with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class AbsNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(t.chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  val out_reg = Reg(t.chiselRepr())
  when(I.asSInt() < 0.S) { out_reg := 0.U - I }.otherwise( out_reg := I )
  O := out_reg
}

/**
  * Not of an Bit atom
  */
class Not() extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STBit().chiselRepr()))
  override val O = IO(Output(STBit().chiselRepr()))
  O := ~I
  valid_down := valid_up
}

/**
  * Not of an Bit atom with no valid interface
  */
class NotNoValid() extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STBit().chiselRepr()))
  override val O = IO(Output(STBit().chiselRepr()))
  O := ~I
}
