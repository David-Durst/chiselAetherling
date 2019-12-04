package aetherling.modules.higherorder

import aetherling.modules.helpers.{BinaryInterface, ValidInterface}
import chisel3._

class Map2S(n: Int, t: => MultiIOModule with BinaryInterface with ValidInterface)
  extends MultiIOModule with BinaryInterface with ValidInterface {
  val ops = (0 to (n-1)).map(_ => Module(t))
  val fst_op = ops.head

  override val in0 = IO(Input(Vec(n, chiselTypeOf(fst_op.in0))))
  override val in1 = IO(Input(Vec(n, chiselTypeOf(fst_op.in1))))
  override val out = IO(Output(Vec(n, chiselTypeOf(fst_op.out))))

  ops.zipWithIndex.foreach { case (op, i) =>
    op.valid_up := valid_up
    op.in0 := in0(i)
    op.in1 := in1(i)
    out(i) := op.out
  }
  valid_down := ops map { op => op.valid_down } reduce { (vdown0, vdown1) => vdown0 & vdown1 }
}
