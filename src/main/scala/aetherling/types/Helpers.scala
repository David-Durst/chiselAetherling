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

  def getFstTuple(data: Data): Data = data.asInstanceOf[TupleBundle].t0b
  def getSndTuple(data: Data): Data = data.asInstanceOf[TupleBundle].t1b
  def getTupleElem(tupleBundle: Data): IndexedSeq[Data] =
    Vector(getFstTuple(tupleBundle), getSndTuple(tupleBundle))
}

class TupleBundle(val t0bd: STTypeDefinition, val t1bd: STTypeDefinition) extends Bundle {
  val t0b = t0bd.chiselRepr()
  val t1b = t1bd.chiselRepr()
}
