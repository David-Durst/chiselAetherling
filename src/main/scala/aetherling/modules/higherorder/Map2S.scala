package aetherling.modules.higherorder

import aetherling.modules.helpers.{BinaryInterface, ValidInterface}
import chisel3._

class Map2S(n: Int, t: => MultiIOModule with BinaryInterface with ValidInterface)
  extends MultiIOModule with BinaryInterface with ValidInterface {
  val ops = (0 to (n-1)).map(_ => Module(t))
  val fst_op = ops.head

  override val I0 = IO(Input(Vec(n, chiselTypeOf(fst_op.I0))))
  override val I1 = IO(Input(Vec(n, chiselTypeOf(fst_op.I1))))
  override val O = IO(Output(Vec(n, chiselTypeOf(fst_op.O))))

  ops.zipWithIndex.foreach { case (op, i) =>
    op.valid_up := valid_up
    op.I0 := I0(i)
    op.I1 := I1(i)
    O(i) := op.O
  }
  valid_down := ops map { op => op.valid_down } reduce { (vdown0, vdown1) => vdown0 & vdown1 }
}
