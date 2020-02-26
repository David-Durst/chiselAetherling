package aetherling.modules

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._

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

/**
  * And of two Bit atoms
  */
class And() extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(STBit(), STBit()).chiselRepr()))
  override val O = IO(Output(STBit().chiselRepr()))
  O := I.t0b.asInstanceOf[Bool] && I.t1b.asInstanceOf[Bool]
  valid_down := valid_up
}

/**
  * And of two Bit atoms with no valid interface
  */
class AndNoValid() extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(STBit(), STBit()).chiselRepr()))
  override val O = IO(Output(STBit().chiselRepr()))
  O := I.t0b.asInstanceOf[Bool] && I.t1b.asInstanceOf[Bool]
}

/**
  * Or of two Bit atoms
  */
class Or() extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(STBit(), STBit()).chiselRepr()))
  override val O = IO(Output(STBit().chiselRepr()))
  O := I.t0b.asInstanceOf[Bool] || I.t1b.asInstanceOf[Bool]
  valid_down := valid_up
}

/**
  * Or of two Bit atoms with no valid interface
  */
class OrNoValid() extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(STBit(), STBit()).chiselRepr()))
  override val O = IO(Output(STBit().chiselRepr()))
  O := I.t0b.asInstanceOf[Bool] || I.t1b.asInstanceOf[Bool]
}

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
  * Sub two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Sub(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() - I.t1b.asUInt()
  valid_down := valid_up
}

/**
  * Sub two Int atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class SubNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() - I.t1b.asUInt()
}

/**
  * Mul two Int atoms with a two cycle delay
  * @param t the Space-Time Int type (specifies width)
  */
class Mul(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := RegNext(RegNext(I.t0b.asUInt()) * RegNext(I.t1b.asUInt()))
  valid_down := RegNext(RegNext(valid_up))
}

/**
  * Mul two Int atoms with a two cycle delay with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class MulNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := RegNext(RegNext(I.t0b.asUInt()) * RegNext(I.t1b.asUInt()))
}

/**
  * Div two Int atoms with a one cycle delay
  * @param t the Space-Time Int type (specifies width)
  */
class Div(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := RegNext(I.t0b.asUInt() / I.t1b.asUInt())
  valid_down := RegNext(valid_up)
}

/**
  * Div two Int atoms with a two cycle delay with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class DivNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := RegNext(I.t0b.asUInt() / I.t1b.asUInt())
}

/**
  * RShift one Int atom by amount set by other Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class RShift(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() >> I.t1b.asUInt()
  valid_down := valid_up
}

/**
  * RShift one Int atom by amount set by other Int atom with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class RShiftNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() >> I.t1b.asUInt()
}

/**
  * LShift one Int atom by amount set by other Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class LShift(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() << I.t1b.asUInt()
  valid_down := valid_up
}

/**
  * LShift one Int atom by amount set by other Int atom with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class LShiftNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() << I.t1b.asUInt()
}

/**
  * Lt two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Lt(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() < I.t1b.asUInt()
  valid_down := valid_up
}

/**
  * Lt two Int atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class LtNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  O := I.t0b.asUInt() < I.t1b.asUInt()
}

/**
  * Eq two Int or Bit atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Eq(t: STIntOrBit) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  // Ok to cast to UInt as Bool will convert to length 1 vector
  O := I.t0b.asUInt() === I.t1b.asUInt()
  valid_down := valid_up
}

/**
  * Eq two Int or Bit atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class EqNoValid(t: STIntOrBit) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  // Ok to cast to UInt as Bool will convert to length 1 vector
  O := I.t0b.asUInt() === I.t1b.asUInt()
}

/**
  * If two Int or Bit atoms
  * @param t the Space-Time Int type (specifies width)
  */
class If(t: STTypeDefinition) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(STBit(), STAtomTuple(t,t)).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  when( I.t0b.asInstanceOf[Bool])
    { O := I.t1b.asInstanceOf[TupleBundle].t0b }
    .otherwise { O := I.t1b.asInstanceOf[TupleBundle].t1b }
  valid_down := valid_up
}

/**
  * If two Int or Bit atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class IfNoValid(t: STTypeDefinition) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(STBit(), STAtomTuple(t,t)).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  when( I.t0b.asInstanceOf[Bool])
  { O := I.t1b.asInstanceOf[TupleBundle].t0b }
    .otherwise { O := I.t1b.asInstanceOf[TupleBundle].t1b }
}
