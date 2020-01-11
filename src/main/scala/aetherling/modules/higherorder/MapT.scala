package aetherling.modules.higherorder

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import chisel3._

class MapT(t: => MultiIOModule with UnaryInterface with ValidInterface)
  extends MultiIOModule with UnaryInterface with ValidInterface {
    val op = Module(t)

    override val I = IO(Input(chiselTypeOf(op.I)))
    override val O = IO(Output(chiselTypeOf(op.O)))

    op.valid_up := valid_up
    op.I := I
    O := op.O
    valid_down := op.valid_down
}

class MapTNoValid(t: => MultiIOModule with UnaryInterface)
  extends MultiIOModule with UnaryInterface {
    val op = Module(t)

    override val I = IO(Input(chiselTypeOf(op.I)))
    override val O = IO(Output(chiselTypeOf(op.O)))

    op.I := I
    O := op.O
}
