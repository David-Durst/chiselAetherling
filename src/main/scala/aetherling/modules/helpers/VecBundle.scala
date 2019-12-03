package aetherling.modules.helpers

import chisel3._
import chisel3.experimental.DataMirror
import chisel3.internal.requireIsChiselType

import scala.collection.immutable.ListMap

// https://github.com/freechipsproject/chisel3/blob/master/src/test/scala/chiselTests/RecordSpec.scala
final class CustomBundle(elts: (String, Data)*) extends Record {
  val elements = ListMap(elts map { case (field, elt) =>
    requireIsChiselType(elt)
    field -> elt
  }: _*)
  def apply(elt: String): Data = elements(elt)
  override def cloneType: this.type = {
    val cloned = elts.map { case (n, d) => n -> DataMirror.internal.chiselTypeClone(d) }
    (new CustomBundle(cloned: _*)).asInstanceOf[this.type]
  }
}
