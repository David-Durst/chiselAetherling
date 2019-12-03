package aetherling.modules.helpers

import chisel3._

abstract class UnaryModule[T0 <: Data, T1 <: Data](val t0: T0, val t1: T1) extends Module {
  val io = IO(new UnaryBundle(t0, t1))
}
