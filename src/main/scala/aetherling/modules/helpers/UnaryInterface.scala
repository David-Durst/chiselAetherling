package aetherling.modules.helpers

import chisel3._

trait UnaryInterface {
  def in: Data
  def out: Data
}
