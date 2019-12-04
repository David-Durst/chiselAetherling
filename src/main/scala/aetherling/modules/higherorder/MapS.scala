package aetherling.modules.higherorder

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import chisel3._

class MapS(n: Int, t: => MultiIOModule with UnaryInterface with ValidInterface)
  extends MultiIOModule with UnaryInterface with ValidInterface {
  val ops = (0 to (n-1)).map(_ => Module(t))
  val fst_op = ops.head

  override val in = IO(Input(Vec(n, chiselTypeOf(fst_op.in))))
  override val out = IO(Output(Vec(n, chiselTypeOf(fst_op.out))))

  ops.zipWithIndex.foreach { case (op, i) =>
    op.valid_up := valid_up
    op.in := in(i)
    out(i) := op.out
  }
  valid_down := ops map { op => op.valid_down } reduce { (vdown0, vdown1) => vdown0 & vdown1 }
}
