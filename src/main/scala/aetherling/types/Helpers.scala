package aetherling.types

import chisel3._

/**
  * A set of helper functions for Aetherling's Space-Time Types
  */
object Helpers {
  def throughput[T <: STTypeDefinition](t: T): Int = t.length() / t.time()

  def stripVec1(data: Data): Data = {
    data match {
      case d: Vec[Data] if d.length == 1 => stripVec1(d(0))
      case d => d
    }
  }

  /**
    * Get the first element of a tuple. If the tuple is nested inside
    * Vecs of length 1, get the first element with the same nesting.
    */
  def getFstTuple(data: Data): Data = {
    data match {
      case d: Vec[Data] if d.length == 1 => {
        val fstWithoutVec1 = getFstTuple(d(0))
        // wire used to create hardware of desired type
        val vecWire = Wire(Vec(1, chiselTypeOf(fstWithoutVec1)))
        vecWire(0) := fstWithoutVec1
        vecWire
      }
      case d => d.asInstanceOf[TupleBundle].t0b
    }
  }
  /**
    * Get the first element of a tuple. If the tuple is nested inside
    * Vecs of length 1, get the first element with the same nesting.
    */
  def getSndTuple(data: Data): Data = {
    data match {
      case d: Vec[Data] if d.length == 1 => {
        val sndWithoutVec1 = getFstTuple(d(0))
        // wire used to create hardware of desired type
        val vecWire = Wire(Vec(1, chiselTypeOf(sndWithoutVec1)))
        vecWire(0) := sndWithoutVec1
        vecWire
      }
      case d => d.asInstanceOf[TupleBundle].t1b
    }
  }
  def getTupleElem(tupleBundle: Data): IndexedSeq[Data] =
    Vector(getFstTuple(tupleBundle), getSndTuple(tupleBundle))
}

class TupleBundle(val t0bd: STTypeDefinition, val t1bd: STTypeDefinition) extends Bundle {
  val t0b = t0bd.chiselRepr()
  val t1b = t1bd.chiselRepr()
}
