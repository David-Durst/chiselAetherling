package aetherling.modules

import aetherling.modules.helpers.{BinaryInterface, ValidInterface}
import aetherling.types.{STAtomTuple, STTypeDefinition}
import chisel3._

class AtomTuple(t0: STTypeDefinition, t1: STTypeDefinition) extends MultiIOModule with BinaryInterface with ValidInterface {
  override val I0 = IO(Input(t0.chiselRepr()))
  override val I1 = IO(Input(t1.chiselRepr()))
  override val O = IO(Output(STAtomTuple(t0, t1).chiselRepr()))
  O.t0b := I0
  O.t1b := I1
  valid_down := valid_up
}
