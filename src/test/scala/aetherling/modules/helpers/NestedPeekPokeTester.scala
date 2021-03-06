package aetherling.modules.helpers

import Chisel.SInt
import aetherling.types.TupleBundle
import chisel3.experimental.{DataMirror, FixedPoint}
import chisel3.iotesters.PeekPokeTester
import chisel3.{Aggregate, Bool, Data, MultiIOModule, UInt}

abstract class NestedPeekPokeTester[+T <: MultiIOModule](val c: T ) extends PeekPokeTester(c) {
  val int_to_ignore = 253
  def poke_nested(signal: TupleBundle, values: IndexedSeq[_]): Unit = {
    values(0) match {
      case v: IndexedSeq[_] =>
        signal.t0b match {
          case e: TupleBundle => poke_nested(e, v)
          case e: Aggregate => poke_nested(e, v)
          case e => throw new Exception(s"Not a Vec or TupleBundle type trying to poke: $e")
        }
      case v: Int => poke_nested(signal.t0b, BigInt(v))
      case v: BigInt => poke_nested(signal.t0b, v)
      case _ => throw new Exception(s"Cannot poke value ${signal.t0b.getClass.getName}")
    }
    values(1) match {
      case v: IndexedSeq[_] =>
        signal.t1b match {
          case e: TupleBundle => poke_nested(e, v)
          case e: Aggregate => poke_nested(e, v)
          case e => throw new Exception(s"Not a Vec or TupleBundle type trying to poke: $e")
        }
      case v: Int => poke_nested(signal.t1b, BigInt(v))
      case v: BigInt => poke_nested(signal.t1b, v)
      case _ => throw new Exception(s"Cannot poke value ${signal.t1b.getClass.getName}")
    }
  }

  def poke_nested(signal: Aggregate, values: IndexedSeq[_]): Unit = {
    (signal.getElements zip values).foreach{ case (elem, value) =>
      value match {
        case v: IndexedSeq[_] =>
          elem match {
            case e: TupleBundle => poke_nested(e, v)
            case e: Aggregate => poke_nested(e, v)
            case e => throw new Exception(s"Not a Vec or TupleBundle type trying to poke: $e")
          }
        case v: Int => poke_nested(elem, v)
        case v: BigInt => poke_nested(elem, v)
        case _ => throw new Exception(s"Cannot poke value ${elem.getClass.getName}")
      }
    }
  }

  def poke_nested(signal: Data, values: IndexedSeq[_]): Unit = {
    signal match {
      case s: TupleBundle => poke_nested(s, values)
      case s: Aggregate => poke_nested(s, values)
      case s => throw new Exception(s"Can't poke a nested set of values for a type $s")
    }
  }

  def poke_nested(signal: Data, value: Boolean): Unit = {
    if (signal.isInstanceOf[SInt]) {
      poke(signal.asInstanceOf[SInt], boolean2BigInt(value))
    }
    else if (signal.isInstanceOf[Aggregate]) {
      poke_nested(signal.asInstanceOf[Aggregate].getElements(0), value)
    }
    else {
      poke(signal.asInstanceOf[UInt], boolean2BigInt(value))
    }
  }

  def poke_nested(signal: Data, value: Int): Unit = {
    if (signal.isInstanceOf[SInt]) {
      poke(signal.asInstanceOf[SInt], BigInt(value))
    }
    else if (signal.isInstanceOf[Aggregate]) {
      poke_nested(signal.asInstanceOf[Aggregate].getElements(0), value)
    }
    else {
      poke(signal.asInstanceOf[UInt], BigInt(value))
    }
  }

  def poke_nested(signal: Data, value: BigInt): Unit = {
    if (signal.isInstanceOf[SInt]) {
      poke(signal.asInstanceOf[SInt], value)
    }
    else if (signal.isInstanceOf[Aggregate]) {
      poke_nested(signal.asInstanceOf[Aggregate].getElements(0), value)
    }
    else {
      poke(signal.asInstanceOf[UInt], value)
    }
  }

  def expect_nested(signal: TupleBundle, values: IndexedSeq[_]): Unit = {
    values(0) match {
      case v: IndexedSeq[_] =>
        signal.t0b match {
          case e: TupleBundle => expect_nested(e, v)
          case e: Aggregate => expect_nested(e, v)
          case e => throw new Exception(s"Not a Vec or TupleBundle type trying to expect: $e")
        }
      case v: Int => expect_nested(signal.t0b, BigInt(v))
      case v: BigInt => expect_nested(signal.t0b, v)
      case _ => throw new Exception(s"Cannot expect value ${signal.t0b.getClass.getName}")
    }
    values(1) match {
      case v: IndexedSeq[_] =>
        signal.t1b match {
          case e: TupleBundle => expect_nested(e, v)
          case e: Aggregate => expect_nested(e, v)
          case e => throw new Exception(s"Not a Vec or TupleBundle type trying to expect: $e")
        }
      case v: Int => expect_nested(signal.t1b, BigInt(v))
      case v: BigInt => expect_nested(signal.t1b, v)
      case _ => throw new Exception(s"Cannot expect value ${signal.t1b.getClass.getName}")
    }
  }

  def expect_nested(signal: Aggregate, values: IndexedSeq[_]): Unit = {
    (signal.getElements zip values).foreach{ case (elem, value) =>
      value match {
        case v: IndexedSeq[_] =>
          elem match {
            case e: TupleBundle => expect_nested(e, v)
            case e: Aggregate => expect_nested(e, v)
            case e => throw new Exception(s"Not a Vec or TupleBundle type trying to expect: $e")
          }
        case v: Int => expect_nested(elem, v)
        case v: BigInt => expect_nested(elem, v)
        case v: Tuple2[BigInt, Tuple2[BigInt, BigInt]] => expect_nested(elem, v)
        case _ => throw new Exception(s"Cannot expect value ${elem.getClass.getName}")
      }
    }
  }

  def expect_nested(signal: Data, values: IndexedSeq[_]): Unit = {
    signal match {
      case s: TupleBundle => expect_nested(s, values)
      case s: Aggregate => expect_nested(s, values)
      case s => throw new Exception(s"Can't expect a nested set of values for a type $s")
    }
  }

  def expect_nested(signal: Data, value: Boolean): Unit = {
    if (value != int_to_ignore) {
      if (signal.isInstanceOf[SInt]) {
        expect(signal.asInstanceOf[SInt], boolean2BigInt(value))
      }
      else if (signal.isInstanceOf[Aggregate]) {
        expect_nested(signal.asInstanceOf[Aggregate].getElements(0), value)
      }
      else {
        expect(signal.asInstanceOf[UInt], boolean2BigInt(value))
      }
    }
  }
  def expect_nested(signal: Data, value: Int): Unit =
  {
    if (value != int_to_ignore) {
      if (signal.isInstanceOf[SInt]) {
        expect(signal.asInstanceOf[SInt], BigInt(value))
      }
      else if (signal.isInstanceOf[Aggregate]) {
        expect_nested(signal.asInstanceOf[Aggregate].getElements(0), value)
      }
      else {
        expect(signal.asInstanceOf[UInt], BigInt(value))
      }
    }
  }
  def expect_nested(signal: Data, value: BigInt): Unit = {
    if (value != int_to_ignore) {
      if (signal.isInstanceOf[SInt]) {
        expect(signal.asInstanceOf[SInt], value)
      }
      else if (signal.isInstanceOf[Aggregate]) {
        expect_nested(signal.asInstanceOf[Aggregate].getElements(0), value)
      }
      else {
        expect(signal.asInstanceOf[UInt], value)
      }
    }
  }
  def expect_nested(signal: Data, value: Tuple2[BigInt, Tuple2[BigInt, BigInt]]): Unit = {
    if (signal.isInstanceOf[TupleBundle] && signal.asInstanceOf[TupleBundle].t0b.isInstanceOf[SInt]) {
      if (value._1 != int_to_ignore) {
        expect(signal.asInstanceOf[TupleBundle].t0b.asInstanceOf[SInt], value._1)
        expect(signal.asInstanceOf[TupleBundle].t1b.asInstanceOf[TupleBundle].t0b.asInstanceOf[SInt], value._2._1)
        expect(signal.asInstanceOf[TupleBundle].t1b.asInstanceOf[TupleBundle].t1b.asInstanceOf[SInt], value._2._2)
      }
    }
    else if (signal.isInstanceOf[TupleBundle]) {
      if (value._1 != int_to_ignore) {
        expect(signal.asInstanceOf[TupleBundle].t0b.asInstanceOf[UInt], value._1)
        expect(signal.asInstanceOf[TupleBundle].t1b.asInstanceOf[TupleBundle].t0b.asInstanceOf[UInt], value._2._1)
        expect(signal.asInstanceOf[TupleBundle].t1b.asInstanceOf[TupleBundle].t1b.asInstanceOf[UInt], value._2._2)
      }
    }
    else {
      expect_nested(signal.asInstanceOf[Aggregate].getElements(0), value)
    }
  }

  def peek_binary_module(t: MultiIOModule with BinaryInterface with ValidInterface): Unit = {
    println(s"in0: ${peek_str(t.I0)}")
    println(s"in1: ${peek_str(t.I1)}")
    println(s"out: ${peek_str(t.O)}")
    println(s"valid_up: ${peek_str(t.valid_up)}")
    println(s"valid_down: ${peek_str(t.valid_down)}")
  }

  def peek_unary_module(t: MultiIOModule with UnaryInterface with ValidInterface): Unit = {
    println(s"in: ${peek_str(t.I)}")
    println(s"out: ${peek_str(t.O)}")
    println(s"valid_up: ${peek_str(t.valid_up)}")
    println(s"valid_down: ${peek_str(t.valid_down)}")
  }

  def peek_nullary_module(t: MultiIOModule with NullaryInterface with ValidInterface): Unit = {
    println(s"out: ${peek_str(t.O)}")
    println(s"valid_up: ${peek_str(t.valid_up)}")
    println(s"valid_down: ${peek_str(t.valid_down)}")
  }

  def peek_any_module(t: MultiIOModule): Unit = {
    DataMirror.modulePorts(t).foreach { case (name, port) => {
      println(s"$name: ${peek_str(port)}")
    }}
  }

  def peek_str(signal: Data): String = {
    signal match {
      case s: TupleBundle => s"Tuple(${peek_str(s.t0b)}, ${peek_str(s.t1b)})"
      case s: Aggregate => s"Vec(${s.getElements.map(peek_str).reduce((l,r) => l + ", " + r)})"
      case s: UInt => peek(s).toString
      case s: SInt => peek(s).toString
      case s: FixedPoint => peek(s).toString
      case s => s"Cannot peek_str $s which has class ${s.getClass}"
    }
  }

  def boolean2BigInt(in: Boolean) = in match {
    case true => BigInt(1)
    case false => BigInt(0)
  }

  /**
    * Given a flat indexed seq, return a nested one
    * @param values
    * @return
    */
  def nest_indexed_seq(values: IndexedSeq[_], nesting_per_layer: IndexedSeq[Int]): IndexedSeq[_] = {
    if (nesting_per_layer.size <= 1) {
      // if at bottom layer of nesting or no nesting, just return
      values
    }
    else {
      // if not at bottom layer, group and operate on elements of group
      val grouped_values = values.grouped(nesting_per_layer(1))
      grouped_values map {(group: IndexedSeq[_]) => nest_indexed_seq(group, nesting_per_layer.tail)} toIndexedSeq
    }
  }

  def nest_indexed_seq(values: BigInt, nesting_per_layer: IndexedSeq[Int]) = values
  def nest_indexed_seq(values: Int, nesting_per_layer: IndexedSeq[Int]) = values
  def nest_indexed_seq(values: Boolean, nesting_per_layer: IndexedSeq[Int]) = values
  def nest_indexed_seq(values: Tuple2[BigInt,Tuple2[BigInt,BigInt]], nesting_per_layer: IndexedSeq[Int]) = values


  /**
    * The number of UInt, Bool, and Tuple elements per layer of Vec
    * @param signal The nested chisel value to inspect
    */
  def compute_num_atoms_per_sseq_layer(signal: Data): IndexedSeq[Int] = {
    signal match {
      case s: TupleBundle => Array[Int]()
      case s: Aggregate =>  {
        val signal_elements = s.getElements
        val lower_layer_elements =
          signal_elements(0).isInstanceOf[Aggregate] match {
            case true => compute_num_atoms_per_sseq_layer(signal_elements(0).asInstanceOf[Aggregate])
            case false => compute_num_atoms_per_sseq_layer(signal_elements(0))
          }
        val top_layer_num_elements = if (lower_layer_elements.isEmpty) 1 else lower_layer_elements(0)
        (signal_elements.size * top_layer_num_elements) +: lower_layer_elements
      }
      case s => Array[Int]()
    }
  }

}
