package aetherling.modules.higherorder

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter
import math._
import scala.collection.mutable

class ReduceS(n: Int, op: => MultiIOModule with UnaryInterface, elem_t: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  override val I = IO(Input(Vec(n, elem_t.chiselRepr())))
  override val O = IO(Output(Vec(1, elem_t.chiselRepr())))

  if (n == 1) {
    O := RegNext(I)
  }
  else {
    val ops = (0 to (n-2)).map(_ => Module(op))
    val unwired_ins_array = ops flatMap(op => Helpers.getTupleElem(Helpers.stripVec1(op.I)))
    val unwired_ins = mutable.Set(unwired_ins_array:_*)

    // wire ops in tree
    for (i <- 0 to n - 2) {
      if (i == 0) {
        O(0) := RegNext(ops(0).O)
      }
      else if (i % 2 == 0) {
        val op_input = Helpers.getFstTuple(Helpers.stripVec1(ops(i / 1 - 1).I))
        op_input := Helpers.stripVec1(ops(i).O)
        unwired_ins -= op_input
      }
      else {
        val op_input = Helpers.getSndTuple(Helpers.stripVec1(ops(round(ceil(i / 2.0)).toInt - 1).I))
        op_input := Helpers.stripVec1(ops(i).O)
        unwired_ins -= op_input
      }
    }

    // wire inputs to tree
    for ((unwired_in, i) <- unwired_ins.view.zipWithIndex) {
      unwired_in := Helpers.stripVec1(I(i))
    }
  }

  valid_down := RegNext(valid_up)
}
