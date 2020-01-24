package aetherling.modules.higherorder

import aetherling.modules.helpers.{BinaryInterface, ValidInterface}
import chisel3._
import chisel3.experimental.CloneModuleAsRecord

class Map2S(n: Int, t: => MultiIOModule with BinaryInterface with ValidInterface)
  extends MultiIOModule with BinaryInterface with ValidInterface {
  val fst_op = Module(t)
  val other_ops = if (n == 1) Vector() else (0 to (n-2)).map(_ => CloneModuleAsRecord(fst_op))

  override val I0 = IO(Input(Vec(n, chiselTypeOf(fst_op.I0))))
  override val I1 = IO(Input(Vec(n, chiselTypeOf(fst_op.I1))))
  override val O = IO(Output(Vec(n, chiselTypeOf(fst_op.O))))

  fst_op.valid_up := valid_up
  fst_op.I0 := I0(0)
  fst_op.I1 := I1(0)
  O(0) := fst_op.O
  other_ops.zipWithIndex.foreach { case (op, i) =>
    op("valid_up").asInstanceOf[Bool] := valid_up
    op("I0").asInstanceOf[fst_op.I0.type] := I0(i+1)
    op("I1").asInstanceOf[fst_op.I1.type] := I1(i+1)
    O(i+1) := op("O").asInstanceOf[fst_op.O.type]
  }
  valid_down := other_ops.foldLeft(fst_op.valid_down) { case (vdown, op) => vdown && op("valid_down").asInstanceOf[Bool] }
}
