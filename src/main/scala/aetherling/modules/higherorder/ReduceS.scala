package aetherling.modules.higherorder

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.experimental.CloneModuleAsRecord
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
    val fst_op = Module(op)
    val other_ops = if (n == 1) Vector() else (0 to (n-2)).map(_ => CloneModuleAsRecord(fst_op))
    val unwired_ins = mutable.Set(0 to 2 * n - 1:_*)

    def get_I_from_op_i(i: Int) = {
      if (i == 0) {
        fst_op.I
      }
      else {
        other_ops(i-1)("I").asInstanceOf[fst_op.I.type]
      }
    }

    // wire ops in tree
    for (i <- 0 to n - 2) {
      if (i == 0) {
        O(0) := RegNext(op.O)
      }
      else if (i % 2 == 0) {
        val op_input = Helpers.getFstTuple(Helpers.stripVec1(get_I_from_op_i(i / 2 - 1)))
        op_input := Helpers.stripVec1(other_ops(i)("O").asInstanceOf[fst_op.O.type])
        unwired_ins -= i
      }
      else {
        val op_input = Helpers.getSndTuple(Helpers.stripVec1(get_I_from_op_i(round(ceil(i / 2.0)).toInt - 1)))
        op_input := Helpers.stripVec1(other_ops(i)("O").asInstanceOf[fst_op.O.type])
        unwired_ins -= i
      }
    }

    // wire inputs to tree
    for (unwired_in <- unwired_ins) {
      val 
      unwired_in := Helpers.stripVec1(I(i))
    }
  }

  valid_down := RegNext(valid_up)
}
