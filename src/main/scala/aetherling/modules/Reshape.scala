package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

class Reshape(t_in: STTypeDefinition, t_out: STTypeDefinition ) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = ???
  val O = ???
}
