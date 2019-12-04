package aetherling.types

import chisel3._

/**
  * A set of helper functions for Aetherling's Space-Time Types
  */
class Helpers {
  def throughput[T <: STTypeDefinition](t: T): Int = t.length() / t.time()
}

class TupleBundle(val t0b: Data, val t1b: Data) extends Bundle
