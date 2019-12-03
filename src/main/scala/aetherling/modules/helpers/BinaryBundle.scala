package aetherling.modules.helpers

import chisel3._

class BinaryBundle[T0 <: Data, T1 <: Data, T2 <: Data](val t0: T0, val t1: T1, val t2: T2) extends Bundle {
  val in0 = Input(t0)
  val in1 = Input(t1)
  val out = Output(t2)
  val valids = new ValidPorts
}
