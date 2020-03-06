package aetherling.modules.higherorder

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter
import math._
import scala.collection.mutable
class ReduceT(n: Int, i: Int, op: => MultiIOModule with UnaryInterface, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  override val I = IO(Input(elem_t.chiselRepr()))
  override val O = IO(Output(elem_t.chiselRepr()))

  val undelayed_out = Wire(elem_t.chiselRepr())
  if (n == 1) {
    undelayed_out := I
    valid_down := RegNext(valid_up, false.B)
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
    val accum_reg = Reg(elem_t.chiselRepr())
    when (valid_up) { accum_reg := op_output_or_module_input }
    Helpers.getFstTuple(Helpers.stripVec1(op_inst.I)) := Helpers.stripVec1(I)
    Helpers.getSndTuple(Helpers.stripVec1(op_inst.I)) := Helpers.stripVec1(accum_reg)
    undelayed_out := op_inst.O

    // finished when elem_counter is n-1 as that is last element and combinational path from op
    // reg it so that no combinational path out of reduce
    // once valid always valid, following the aetherling valid interface
    val valid_reg = RegInit(Bool(), false.B)
    valid_reg := valid_reg || (elem_counter_value === (n-1).U)
    valid_down := valid_reg
  }

  O := RegNext(undelayed_out)
}
