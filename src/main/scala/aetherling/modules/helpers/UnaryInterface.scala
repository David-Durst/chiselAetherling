package aetherling.modules.helpers

import chisel3._

trait UnaryInterface {
  val in: Data
  val out: Data
}
