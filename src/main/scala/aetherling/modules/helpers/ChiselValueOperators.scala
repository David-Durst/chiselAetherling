package aetherling.modules.helpers

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

object ChiselValueOperators {

  /**
    * Given a chisel value containing aggregates, return a Vector with all the non-aggregate Data values
    */
  def flattenChiselValue(valueToFlatten: Data): IndexedSeq[Data] = {
    valueToFlatten match {
      case v: Aggregate => v.getElements flatMap flattenChiselValue toIndexedSeq
      case v: Data => Vector(v)
    }
  }
}
