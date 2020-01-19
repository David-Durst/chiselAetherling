package aetherling.modules.higherorder

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter
import math._
import scala.collection.mutable

class ReduceS(n: Int, op: => MultiIOModule with UnaryInterface) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(Vec(n, chiselTypeOf(Helpers.getFstTuple(op.I)))))
  override val O = IO(Output(Vec(n, chiselTypeOf(op.O))))

  if (n == 1) {
    O := RegNext(I)
  }
  else {
    val ops = (0 to (n-1)).map(_ => Module(op))
    val unwired_ins_array = ops flatMap(op => Helpers.getTupleElem(op.I))
    val unwired_ins = mutable.Set(unwired_ins_array:_*)

    // wire ops into tree
    for (i <- 0 to n - 1) {
      if (i == 0) {
        O := RegNext(ops(0).O)
      }
      else if (i % 2 == 0) {
        val op_input = Helpers.getFstTuple(ops(i / 2 - 1).I)
        op_input := ops(i).O
        unwired_ins -= op_input
      }
      else {
        val op_input = Helpers.getSndTuple(ops(round(ceil(i / 2.0)).toInt - 1).I)
        op_input := ops(i).O
        unwired_ins -= op_input
      }
    }

    // wire inputs to tree
    for ((unwired_in, i) <- unwired_ins.view.zipWithIndex) {
      unwired_in := I(i)
    }
  }

  valid_down := RegNext(valid_up)
}
