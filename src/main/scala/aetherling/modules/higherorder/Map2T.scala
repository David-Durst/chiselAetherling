package aetherling.modules.higherorder

import aetherling.modules.helpers.{BinaryInterface, ValidInterface}
import chisel3._

class Map2T(t: => MultiIOModule with BinaryInterface with ValidInterface)
  extends MultiIOModule with BinaryInterface with ValidInterface {
    val op = Module(t)

    override val I0 = IO(Input(chiselTypeOf(op.I0)))
    override val I1 = IO(Input(chiselTypeOf(op.I1)))
    override val O = IO(Output(chiselTypeOf(op.O)))

    op.valid_up := valid_up
    op.I0 := I0
    op.I1 := I1
    O := op.O
    valid_down := op.valid_down
}
