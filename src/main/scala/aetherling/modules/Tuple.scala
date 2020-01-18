package aetherling.modules

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._

class SSeqTupleCreator(t: STTypeDefinition) extends MultiIOModule with BinaryInterface with ValidInterface {
  override val I0 = IO(Input(t.chiselRepr()))
  override val I1 = IO(Input(t.chiselRepr()))
  override val O = IO(Output(SSeq_Tuple(2, t).chiselRepr()))

  O.asInstanceOf[Vec[Data]](0) := I0
  O.asInstanceOf[Vec[Data]](1) := I1

  valid_down := valid_up
}

class SSeqTupleAppender(t: STTypeDefinition, n: Int) extends MultiIOModule with BinaryInterface with ValidInterface {
  override val I0 = IO(Input(SSeq_Tuple(n, t).chiselRepr()))
  override val I1 = IO(Input(t.chiselRepr()))
  override val O = IO(Output(SSeq_Tuple(n+1, t).chiselRepr()))

  for (i <- 0 to n-1) {
    O.asInstanceOf[Vec[Data]](i) := I0.asInstanceOf[Vec[Data]](i)
  }
  O.asInstanceOf[Vec[Data]](n) := I1

  valid_down := valid_up
}

class SSeqToSSeqTuple(t: STTypeDefinition, n: Int) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(SSeq_Tuple(n, t).chiselRepr()))
  override val O = IO(Output(SSeq(n, t).chiselRepr()))
  O := I
  valid_down := valid_up
}

class SSeqTupleToSSeq(t: STTypeDefinition, n: Int) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(SSeq(n, t).chiselRepr()))
  override val O = IO(Output(SSeq_Tuple(n, t).chiselRepr()))
  O := I
  valid_down := valid_up
}

class AtomTuple(t0: STTypeDefinition, t1: STTypeDefinition) extends MultiIOModule with BinaryInterface with ValidInterface {
  override val I0 = IO(Input(t0.chiselRepr()))
  override val I1 = IO(Input(t1.chiselRepr()))
  override val O = IO(Output(STAtomTuple(t0, t1).chiselRepr()))
  O.t0b := I0
  O.t1b := I1
  valid_down := valid_up
}

class Fst[T0 <: STTypeDefinition, T1 <: STTypeDefinition](t: STAtomTuple[T0, T1]) extends MultiIOModule
  with UnaryInterface with ValidInterface  {
  override val I = IO(Input(t.chiselRepr()))
  override val O = IO(Output(t.t0.chiselRepr()))
  O := I.t0b
  valid_down := valid_up
}

class Snd[T0 <: STTypeDefinition, T1 <: STTypeDefinition](t: STAtomTuple[T0, T1]) extends MultiIOModule
  with UnaryInterface with ValidInterface  {
  override val I = IO(Input(t.chiselRepr()))
  override val O = IO(Output(t.t1.chiselRepr()))
  O := I.t1b
  valid_down := valid_up
}
