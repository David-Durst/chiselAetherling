package aetherling.modules.helpers

import chisel3._

trait BinaryInterface {
  def in0: Data
  def in1: Data
  def out: Data
}
