package aetherling.modules

import aetherling.modules.helpers.{BinaryInterface, ValidInterface}
import aetherling.types.{STAtomTuple, STTypeDefinition}
import chisel3._

class AtomTuple(t0: STTypeDefinition, t1: STTypeDefinition) extends MultiIOModule with BinaryInterface with ValidInterface {
  override val in0 = IO(Input(t0.chiselRepr()))
  override val in1 = IO(Input(t1.chiselRepr()))
  override val out = IO(Output(STAtomTuple(t0, t1).chiselRepr()))
  out.t0b := in0
  out.t1b := in0
  valid_down := valid_up
}
