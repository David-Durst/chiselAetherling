package aetherling.modules

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._
import chisel3.util.{Cat, HasBlackBoxResource}
import chisel3.experimental.DataMirror

import scala.io.Source

/**
  * Abs of an Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class Abs(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(t.chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  val out_reg = Reg(t.chiselRepr())
  when(I.asInstanceOf[SInt] < 0.S) { out_reg := 0.S - I.asInstanceOf[SInt] }
    .otherwise( out_reg := I.asInstanceOf[SInt] )
  O := out_reg
  valid_down := RegNext(valid_up, false.B)
}

/**
  * Abs of an Int atom with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class AbsNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(t.chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  val out_reg = Reg(t.chiselRepr())
  when(I.asInstanceOf[SInt] < 0.S) { out_reg := 0.S - I.asInstanceOf[SInt] }
    .otherwise( out_reg := I.asInstanceOf[SInt] )
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
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] + I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() + I.t1b.asUInt()
  }
  valid_down := valid_up
}

/**
  * Add two Int atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class AddNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] + I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() + I.t1b.asUInt()
  }
}

/**
  * Sub two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Sub(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] - I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() - I.t1b.asUInt()
  }
  valid_down := valid_up
}

/**
  * Sub two Int atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class SubNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] - I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() - I.t1b.asUInt()
  }
}

/**
  * Mul two Int atoms with a two cycle delay
  * @param t the Space-Time Int type (specifies width)
  */
class Mul(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (!t.signed && t.width == 8) {
    val inner_mul = Module(new BlackBoxMulUInt8)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(7,0)
    inner_mul.io.clock := clock
  }
  else if (t.signed && t.width == 8) {
    Module(new BlackBoxMulInt8)
    val inner_mul = Module(new BlackBoxMulInt8)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(7,0)
    inner_mul.io.clock := clock
  }
  else if (!t.signed && t.width == 16) {
    val inner_mul = Module(new BlackBoxMulUInt16)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(15,0)
    inner_mul.io.clock := clock
  }
  else if (t.signed && t.width == 16) {
    Module(new BlackBoxMulInt8)
    val inner_mul = Module(new BlackBoxMulInt16)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(15,0)
    inner_mul.io.clock := clock
  }
  else if (!t.signed && t.width == 32) {
    val inner_mul = Module(new BlackBoxMulUInt32)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(31,0)
    inner_mul.io.clock := clock
  }
  else if (t.signed && t.width == 32) {
    Module(new BlackBoxMulInt8)
    val inner_mul = Module(new BlackBoxMulInt32)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(31,0)
    inner_mul.io.clock := clock
  }
  else {
    ???
  }

  if (t.width == 32) {
    valid_down := RegNext(RegNext(RegNext(RegNext(RegNext(RegNext(valid_up, false.B), false.B), false.B))))
  }
  else {
    valid_down := RegNext(RegNext(RegNext(valid_up, false.B), false.B), false.B)
  }
}

/**
  * Mul two Int atoms with a two cycle delay with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class MulNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (!t.signed && t.width == 8) {
    val inner_mul = Module(new BlackBoxMulUInt8)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O
    inner_mul.io.clock := clock
  }
  else if (t.signed && t.width == 8) {
    val inner_mul = Module(new BlackBoxMulInt8)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O
    inner_mul.io.clock := clock
  }
  else if (!t.signed && t.width == 16) {
    val inner_mul = Module(new BlackBoxMulUInt16)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(15,0)
    inner_mul.io.clock := clock
  }
  else if (t.signed && t.width == 16) {
    val inner_mul = Module(new BlackBoxMulInt16)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(15,0)
    inner_mul.io.clock := clock
  }
  else if (!t.signed && t.width == 32) {
    val inner_mul = Module(new BlackBoxMulUInt32)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(31,0)
    inner_mul.io.clock := clock
  }
  else if (t.signed && t.width == 32) {
    val inner_mul = Module(new BlackBoxMulInt32)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := I.t1b
    O := inner_mul.io.O(31,0)
    inner_mul.io.clock := clock
  }
  else {
    ???
  }
}

class BlackBoxMulUInt8 extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    val I0 = Input(UInt(8.W))
    val I1 = Input(UInt(8.W))
    val O = Output(UInt(16.W))
    val clock = Input(Clock())
  })
  addResource("/verilogAetherling/mul.v")
}

class BlackBoxMulInt8 extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    val I0 = Input(SInt(8.W))
    val I1 = Input(SInt(8.W))
    val O = Output(SInt(16.W))
    val clock = Input(Clock())
  })
  addResource("verilogAetherling/mul.v")
}

class BlackBoxMulUInt16 extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    val I0 = Input(UInt(16.W))
    val I1 = Input(UInt(16.W))
    val O = Output(UInt(32.W))
    val clock = Input(Clock())
  })
  addResource("/verilogAetherling/mul.v")
}

class BlackBoxMulInt16 extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    val I0 = Input(SInt(16.W))
    val I1 = Input(SInt(16.W))
    val O = Output(SInt(32.W))
    val clock = Input(Clock())
  })
  addResource("verilogAetherling/mul.v")
}

class BlackBoxMulUInt32 extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    val I0 = Input(UInt(32.W))
    val I1 = Input(UInt(32.W))
    val O = Output(UInt(64.W))
    val clock = Input(Clock())
  })
  addResource("/verilogAetherling/mul.v")
}

class BlackBoxMulInt32 extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle() {
    val I0 = Input(SInt(32.W))
    val I1 = Input(SInt(32.W))
    val O = Output(SInt(64.W))
    val clock = Input(Clock())
  })
  addResource("verilogAetherling/mul.v")
}
/**
  * Div two Int atoms with a one cycle delay
  * @param t the Space-Time Int type (specifies width)
  */
class Div(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,STFixP1_7()).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (!t.signed && t.width == 8) {
    val inner_mul = Module(new BlackBoxMulUInt8)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := Cat(0.U, I.t1b.asUInt())
    O := inner_mul.io.O(14,7)
    inner_mul.io.clock := clock
  }
  else if (!t.signed && t.width == 16) {
    val inner_mul = Module(new BlackBoxMulUInt16)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := Cat(0.U, I.t1b.asUInt())
    O := inner_mul.io.O(22,7)
    inner_mul.io.clock := clock
  }
  else if (!t.signed && t.width == 32) {
    val inner_mul = Module(new BlackBoxMulUInt32)
    inner_mul.io.I0 := I.t0b
    inner_mul.io.I1 := Cat(0.U, I.t1b.asUInt())
    O := inner_mul.io.O(38,7)
    inner_mul.io.clock := clock
  }
  else {
    ???
  }

  if (t.width == 32) {
    valid_down := RegNext(RegNext(RegNext(RegNext(RegNext(RegNext(valid_up, false.B), false.B), false.B))))
  }
  else {
    valid_down := RegNext(RegNext(RegNext(valid_up, false.B), false.B), false.B)
  }
}

/**
  * Div two Int atoms with a two cycle delay with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class DivNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] / I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() / I.t1b.asUInt()
  }
}

/**
  * RShift one Int atom by amount set by other Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class RShift(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,STInt(8, false)).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] >> I.t1b.asUInt
  }
  else {
    O := I.t0b.asUInt() >> I.t1b.asUInt()
  }
  valid_down := valid_up
}

/**
  * RShift one Int atom by amount set by other Int atom with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class RShiftNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,STInt(8, false)).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] >> I.t1b.asUInt
  }
  else {
    O := I.t0b.asUInt() >> I.t1b.asUInt()
  }
}

/**
  * LShift one Int atom by amount set by other Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class LShift(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,STInt(8, false)).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] << I.t1b.asUInt
  }
  else {
    O := I.t0b.asUInt() << I.t1b.asUInt()
  }
  valid_down := valid_up
}

/**
  * LShift one Int atom by amount set by other Int atom with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class LShiftNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,STInt(8, false)).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] << I.t1b.asUInt
  }
  else {
    O := I.t0b.asUInt() << I.t1b.asUInt()
  }
}

/**
  * Lt two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Lt(t: STInt) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] < I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() < I.t1b.asUInt()
  }
  valid_down := valid_up
}

/**
  * Lt two Int atoms with no valid interface
  * @param t the Space-Time Int type (specifies width)
  */
class LtNoValid(t: STInt) extends MultiIOModule with UnaryInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  if (t.signed) {
    O := I.t0b.asInstanceOf[SInt] < I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() < I.t1b.asUInt()
  }
}

/**
  * Eq two Int or Bit atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Eq(t: STIntOrBit) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(STAtomTuple(t,t).chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))
  // Ok to cast to UInt as Bool will convert to length 1 vector
  if (t.isInstanceOf[STInt] && t.asInstanceOf[STInt].signed) {
    O := I.t0b.asInstanceOf[SInt] === I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() === I.t1b.asUInt()
  }
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
  if (t.isInstanceOf[STInt] && t.asInstanceOf[STInt].signed) {
    O := I.t0b.asInstanceOf[SInt] < I.t1b.asInstanceOf[SInt]
  }
  else {
    O := I.t0b.asUInt() < I.t1b.asUInt()
  }
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
