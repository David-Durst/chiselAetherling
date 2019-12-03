package aetherling.modules.helpers

import chisel3._

class UnaryBundle[T0 <: Data, T1 <: Data](val t0: T0, val t1: T1) extends Bundle {
  val in = Input(t0)
  val out = Output(t1)
  val valids = new ValidPorts
}
