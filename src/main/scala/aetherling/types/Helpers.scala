package aetherling.types

/**
  * A set of helper functions for Aetherling's Space-Time Types
  */
class Helpers {
  def throughput[T <: STTypeDefinition](t: T): Int = t.length() / t.time()
}
