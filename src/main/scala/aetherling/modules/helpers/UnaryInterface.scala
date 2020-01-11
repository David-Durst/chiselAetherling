package aetherling.modules.helpers

import chisel3._

trait UnaryInterface {
  val I: Data
  val O: Data
}
