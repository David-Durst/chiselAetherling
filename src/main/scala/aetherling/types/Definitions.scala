package aetherling.types

import chisel3._

abstract class STTypeDefinition {
  /**
    * Total amount of atoms over the entire time of the ST type
    */
  def length(): Int

  /**
    * Number of atoms each active clock
    */
  def portWidth(): Int

  /**
    * Number of clocks required for an operator to accept or emit this type
    */
  def time(): Int

  /**
    * Number of valid clocks in .time() clocks
    * @return
    */
  def validClocks(): Int

  /**
    * A Chisel representation of this type as a nested array of bits.
        Chisel doesn't acount for time.
    */
  def chiselRepr(): Data
}

trait NestedSTType[T <: STTypeDefinition] extends STTypeDefinition {
  val n: Int
  val t: T
}
trait STIntOrBit extends STTypeDefinition

case class TSeq[T <: STTypeDefinition](n: Int, i: Int, t: T) extends STTypeDefinition with NestedSTType[T] {
  /**
    * Total amount of atoms over the entire time of the ST type
    */
  override def length(): Int = n * t.length()

  /**
    * Number of atoms each active clock
    */
  override def portWidth(): Int = t.length()

  /**
    * Number of clocks required for an operator to accept or emit this type
    */
  override def time(): Int = (n+i) * t.time()

  /**
    * Number of valid clocks in .time() clocks
    *
    * @return
    */
  override def validClocks(): Int = n * t.validClocks()

  /**
    * A Chisel representation of this type as a nested array of bits.
    * Chisel doesn't acount for time.
    */
  override def chiselRepr(): Data = t.chiselRepr()
}

case class SSeq[T <: STTypeDefinition](n: Int, t: T) extends STTypeDefinition with NestedSTType[T] {
  /**
    * Total amount of atoms over the entire time of the ST type
    */
  override def length(): Int = n * t.length()

  /**
    * Number of atoms each active clock
    */
  override def portWidth(): Int = n * t.length()

  /**
    * Number of clocks required for an operator to accept or emit this type
    */
  override def time(): Int = t.time()

  /**
    * Number of valid clocks in .time() clocks
    *
    * @return
    */
  override def validClocks(): Int = t.validClocks()

  /**
    * A Chisel representation of this type as a nested array of bits.
    * Chisel doesn't acount for time.
    */
  override def chiselRepr(): Vec[Data] = Vec(n, t.chiselRepr())
}

case class SSeq_Tuple[T <: STTypeDefinition](n: Int, t: T) extends STTypeDefinition with NestedSTType[T] {
  /**
    * Total amount of atoms over the entire time of the ST type
    */
  override def length(): Int = n * t.length()

  /**
    * Number of atoms each active clock
    */
  override def portWidth(): Int = n * t.length()

  /**
    * Number of clocks required for an operator to accept or emit this type
    */
  override def time(): Int = t.time()

  /**
    * Number of valid clocks in .time() clocks
    *
    * @return
    */
  override def validClocks(): Int = t.validClocks()

  /**
    * A Chisel representation of this type as a nested array of bits.
    * Chisel doesn't acount for time.
    */
  override def chiselRepr(): Data = Vec(n, t.chiselRepr())
}

case class STAtomTuple[T0 <: STTypeDefinition, T1 <: STTypeDefinition](t0: T1, t1: T1)
  extends STTypeDefinition {
  /**
    * Total amount of atoms over the entire time of the ST type
    */
  override def length(): Int = t0.length() + t1.length()

  /**
    * Number of atoms each active clock
    */
  override def portWidth(): Int = t0.portWidth() + t1.portWidth()

  /**
    * Number of clocks required for an operator to accept or emit this type
    */
  override def time(): Int = 1

  /**
    * Number of valid clocks in .time() clocks
    *
    * @return
    */
  override def validClocks(): Int = 1

  /**
    * A Chisel representation of this type as a nested array of bits.
    * Chisel doesn't acount for time.
    */
  override def chiselRepr(): TupleBundle = new TupleBundle(t0, t1)
}

case class STInt(width: Int) extends STTypeDefinition with STIntOrBit {
  /**
    * Total amount of atoms over the entire time of the ST type
    */
  override def length(): Int = 1

  /**
    * Number of atoms each active clock
    */
  override def portWidth(): Int = 1

  /**
    * Number of clocks required for an operator to accept or emit this type
    */
  override def time(): Int = 1

  /**
    * Number of valid clocks in .time() clocks
    *
    * @return
    */
  override def validClocks(): Int = 1

  /**
    * A Chisel representation of this type as a nested array of bits.
    * Chisel doesn't acount for time.
    */
  override def chiselRepr(): UInt = UInt(width.W)
}

case class STBit() extends STTypeDefinition with STIntOrBit {
  /**
    * Total amount of atoms over the entire time of the ST type
    */
  override def length(): Int = 1

  /**
    * Number of atoms each active clock
    */
  override def portWidth(): Int = 1

  /**
    * Number of clocks required for an operator to accept or emit this type
    */
  override def time(): Int = 1

  /**
    * Number of valid clocks in .time() clocks
    *
    * @return
    */
  override def validClocks(): Int = 1

  /**
    * A Chisel representation of this type as a nested array of bits.
    * Chisel doesn't acount for time.
    */
  override def chiselRepr(): Bool = Bool()
}
