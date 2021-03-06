package aetherling.modules.higherorder

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._

class Remove10T(op: => MultiIOModule with UnaryInterface with ValidInterface) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val op_inst = Module(op)
  val I = IO(Input(chiselTypeOf(op_inst.I)))
  val O = IO(Output(chiselTypeOf(op_inst.O)))

  op_inst.I := I
  O := op_inst.O
  op_inst.valid_up := valid_up
  valid_down := op_inst.valid_down
}
