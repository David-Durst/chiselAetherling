package aetherling.modules.helpers

import chisel3._

abstract class BinaryModule[T0 <: Data, T1 <: Data, T2 <: Data](val t0: T0, val t1: T1, val t2: T2) extends Module {
  val io = IO(new BinaryBundle(t0, t1, t2))
}
