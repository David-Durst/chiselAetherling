package aetherling.modules.higherorder

import aetherling.modules.helpers.{BinaryInterface, ValidInterface}
import chisel3._

class Map2T(t: => MultiIOModule with BinaryInterface with ValidInterface)
  extends MultiIOModule with BinaryInterface with ValidInterface {
    val op = Module(t)

    override val in0 = IO(Input(chiselTypeOf(op.in0)))
    override val in1 = IO(Input(chiselTypeOf(op.in1)))
    override val out = IO(Output(chiselTypeOf(op.out)))

    op.valid_up := valid_up
    op.in0 := in0
    op.in1 := in1
    out := op.out
    valid_down := op.valid_down
}
