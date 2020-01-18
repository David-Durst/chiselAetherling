package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

class Reshape(t_in: STTypeDefinition, t_out: STTypeDefinition ) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Input(t_in.chiselRepr()))
  val O = IO(Output(t_out.chiselRepr()))
  throw new NotImplementedError("Reshape Not Implemented")
}
