package aetherling.modules.helpers

import chisel3._

trait BinaryInterface {
  val in0: Data
  val in1: Data
  val out: Data
}
