package aetherling.modules.higherorder

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import chisel3._

class MapT[T <: MultiIOModule with UnaryInterface with ValidInterface](t: => T)
  extends MultiIOModule with UnaryInterface with ValidInterface {
    val op = Module(t)

    override val in = IO(Input(op.in))
    override val out = IO(Output(op.out))

    op.valid_up := valid_up
    op.in := in
    out := op.out
    valid_down := op.valid_down
}
