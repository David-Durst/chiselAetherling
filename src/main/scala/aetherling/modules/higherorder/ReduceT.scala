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
    // only increment when on a valid element
    val per_elem_counter = Module(new NestedCountersWithNumValid(elem_t, false))
    //printf(p"per_elem_counter valid: ${per_elem_counter.valid}\n")

    val op_inst = Module(op)
    val (elem_counter_value, _) = Counter(valid_up && per_elem_counter.last, n + i)

    // wire output of op and module input to mux into accum reg
    // wire module input and output of accum reg to op
    // first clock accum reg reads module input so it gets set to first elem
    // later clocks it reads op output
    // output module from op as combinational path from input elem i and sum of elements i-1 from accum
    //  (excluding clock 0, but not outputing on that clock as n==1 case handled separately)
    val op_output_or_module_input = Mux(elem_counter_value === 0.U, I, op_inst.O)
    val accum_reg = Reg(elem_t.chiselRepr())

    per_elem_counter.CE := valid_up
    when (per_elem_counter.valid) { accum_reg := op_output_or_module_input }
    //printf(p"accum_reg: ${accum_reg}\n")
    //printf(p"op_output: ${op_inst.O}\n")
    //printf(p"elem_counter_value: ${elem_counter_value}\n")

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
