package aetherling.modules.higherorder

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter
import math._
import scala.collection.mutable
class ReduceT(n: Int, i: Int, op: => MultiIOModule with UnaryInterface) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  override val I = IO(Input(chiselTypeOf(Helpers.getFstTuple(op.I))))
  override val O = IO(Output(chiselTypeOf(op.O)))

  val undelayed_out = Wire(chiselTypeOf(op.O))
  if (n == 1) {
    undelayed_out := I
    valid_down := RegNext(valid_up)
  }
  else {
    val op_inst = Module(op)
    val (elem_counter_value, _) = Counter(valid_up, n + i)

    // wire output of op and module input to mux into accum reg
    // wire module input and output of accum reg to op
    // first clock accum reg reads module input so it gets set to first elem
    // later clocks it reads op output
    // output module from op as combinational path from input elem i and sum of elements i-1 from accum
    //  (excluding clock 0, but not outputing on that clock as n==1 case handled separately)
    val op_output_or_module_input = Mux(elem_counter_value === 0.U, I, op_inst.O)
    val accum_reg = RegNext(op_output_or_module_input)
    Helpers.getFstTuple(op_inst.I) := I
    Helpers.getSndTuple(op_inst.I) := accum_reg
    undelayed_out := op_inst.O

    // finished when elem_counter is n-1 as that is last element and combinational path from op
    // reg it so that no combinational path out of reduce
    valid_down := RegNext(elem_counter_value === (n-1).U)
  }

  O := RegNext(undelayed_out)
}
