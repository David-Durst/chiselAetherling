package aetherling.modules.helpers

import chisel3._

class ValidPorts extends Bundle {
  val valid_up = Input(Bool())
  val valid_down = Output(Bool())
}
