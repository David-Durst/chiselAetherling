package aetherling.modules.helpers

import chisel3._
import chisel3.experimental.IO

trait ValidInterface {
  val valid_up: Bool = IO(Input(Bool()))
  val valid_down: Bool = IO(Output(Bool()))
}
