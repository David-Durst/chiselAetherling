package aetherling.modules.higherorder

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter
import math._
import scala.collection.mutable
class ReduceT(n: Int, op: => MultiIOModule with UnaryInterface) extends MultiIOModule with UnaryInterface with ValidInterface {
  val ops = (0 to (n-1)).map(_ => Module(op))
  val fst_op = ops.head

  override val I = IO(Input(Vec(n, chiselTypeOf(Helpers.getFstTuple(op.I)))))
  override val O = IO(Output(Vec(n, chiselTypeOf(op.O))))
}
