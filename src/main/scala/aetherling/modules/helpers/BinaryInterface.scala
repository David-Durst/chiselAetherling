package aetherling.modules.helpers

import chisel3._

trait BinaryInterface {
  val I0: Data
  val I1: Data
  val O: Data
}
