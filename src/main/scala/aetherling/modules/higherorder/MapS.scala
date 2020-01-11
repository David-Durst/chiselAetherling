package aetherling.modules.higherorder

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import chisel3._

class MapS(n: Int, t: => MultiIOModule with UnaryInterface with ValidInterface)
  extends MultiIOModule with UnaryInterface with ValidInterface {
  val ops = (0 to (n-1)).map(_ => Module(t))
  val fst_op = ops.head

  override val I = IO(Input(Vec(n, chiselTypeOf(fst_op.I))))
  override val O = IO(Output(Vec(n, chiselTypeOf(fst_op.O))))

  ops.zipWithIndex.foreach { case (op, i) =>
    op.valid_up := valid_up
    op.I := I(i)
    O(i) := op.O
  }
  valid_down := ops map { op => op.valid_down } reduce { (vdown0, vdown1) => vdown0 & vdown1 }
}

class MapSNoValid(n: Int, t: => MultiIOModule with UnaryInterface)
  extends MultiIOModule with UnaryInterface {
  val ops = (0 to (n-1)).map(_ => Module(t))
  val fst_op = ops.head

  override val I = IO(Input(Vec(n, chiselTypeOf(fst_op.I))))
  override val O = IO(Output(Vec(n, chiselTypeOf(fst_op.O))))

  ops.zipWithIndex.foreach { case (op, i) =>
    op.I := I(i)
    O(i) := op.O
  }
}
