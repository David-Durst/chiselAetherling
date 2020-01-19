package aetherling.types

import chisel3._

/**
  * A set of helper functions for Aetherling's Space-Time Types
  */
object Helpers {
  def throughput[T <: STTypeDefinition](t: T): Int = t.length() / t.time()
  def getFstTuple(tupleBundle: Data): Data = tupleBundle.asInstanceOf[TupleBundle].t0b
  def getSndTuple(tupleBundle: Data): Data = tupleBundle.asInstanceOf[TupleBundle].t1b
  def getTupleElem(tupleBundle: Data): IndexedSeq[Data] =
    Vector(getFstTuple(tupleBundle), getSndTuple(tupleBundle))
}

class TupleBundle(val t0bd: STTypeDefinition, val t1bd: STTypeDefinition) extends Bundle {
  val t0b = t0bd.chiselRepr()
  val t1b = t1bd.chiselRepr()
}
