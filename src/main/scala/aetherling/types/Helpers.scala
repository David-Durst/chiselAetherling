package aetherling.types

import chisel3._

/**
  * A set of helper functions for Aetherling's Space-Time Types
  */
class Helpers {
  def throughput[T <: STTypeDefinition](t: T): Int = t.length() / t.time()
}

class TupleBundle(val t0bd: STTypeDefinition, val t1bd: STTypeDefinition) extends Bundle {
  val t0b = t0bd.chiselRepr()
  val t1b = t1bd.chiselRepr()
}
