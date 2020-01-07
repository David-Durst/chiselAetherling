package aetherling.modules.higherorder

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import chisel3._

class MapT(t: => MultiIOModule with UnaryInterface with ValidInterface)
  extends MultiIOModule with UnaryInterface with ValidInterface {
    val op = Module(t)

    override val in = IO(Input(chiselTypeOf(op.in)))
    override val out = IO(Output(chiselTypeOf(op.out)))

    op.valid_up := valid_up
    op.in := in
    out := op.out
    valid_down := op.valid_down
}

class MapTNoValid(t: => MultiIOModule with UnaryInterface)
  extends MultiIOModule with UnaryInterface {
    val op = Module(t)

    override val in = IO(Input(chiselTypeOf(op.in)))
    override val out = IO(Output(chiselTypeOf(op.out)))

    op.in := in
    out := op.out
}
