package aetherling.modules.higherorder

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import chisel3._
import chisel3.experimental.CloneModuleAsRecord

class MapS(n: Int, t: => MultiIOModule with UnaryInterface with ValidInterface)
  extends MultiIOModule with UnaryInterface with ValidInterface {
  val fst_op = Module(t)
  val other_ops = if (n == 1) Vector() else (0 to (n-2)).map(_ => CloneModuleAsRecord(fst_op))

  override val I = IO(Input(Vec(n, chiselTypeOf(fst_op.I))))
  override val O = IO(Output(Vec(n, chiselTypeOf(fst_op.O))))

  fst_op.valid_up := valid_up
  fst_op.I := I(0)
  O(0) := fst_op.O
  other_ops.zipWithIndex.foreach { case (op, i) =>
    op("valid_up").asInstanceOf[Bool] := valid_up
    op("I").asInstanceOf[fst_op.I.type] := I(i+1)
    O(i+1) := op("O").asInstanceOf[fst_op.O.type]
  }
  valid_down := other_ops.foldLeft(fst_op.valid_down) { case (vdown, op) => vdown && op("valid_down").asInstanceOf[Bool] }
}

class MapSNoValid(n: Int, t: => MultiIOModule with UnaryInterface)
  extends MultiIOModule with UnaryInterface {
  val fst_op = Module(t)
  val other_ops = if (n == 1) Vector() else (0 to (n-2)).map(_ => CloneModuleAsRecord(fst_op))

  override val I = IO(Input(Vec(n, chiselTypeOf(fst_op.I))))
  override val O = IO(Output(Vec(n, chiselTypeOf(fst_op.O))))

  fst_op.I := I(0)
  O(0) := fst_op.O
  other_ops.zipWithIndex.foreach { case (op, i) =>
    op("I").asInstanceOf[fst_op.I.type] := I(i+1)
    O(i+1) := op("O").asInstanceOf[fst_op.O.type]
  }
}
