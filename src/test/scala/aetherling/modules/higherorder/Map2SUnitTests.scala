package aetherling.modules.higherorder

import aetherling.modules.{Abs, AtomTuple}
import aetherling.types.{STInt, TupleBundle}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester, Pokeable}

abstract class NestedPeekPokeTester[+T <: MultiIOModule](val c: T ) extends PeekPokeTester(c) {
  /*
  def poke_nested(signal: Aggregate, values: IndexedSeq[Int]): Unit = {
    poke(signal, values map { x => BigInt(x)})
  }
   */

  def poke_nested[TS](signal: TupleBundle, values: IndexedSeq[TS]): Unit = {
    values(0) match {
      case v: IndexedSeq[_] =>
        signal.t0b match {
          case e: Aggregate => poke_nested(e, v)
          case e: TupleBundle => poke_nested(e, v)
          case e => throw new Exception(s"Not a Vec or TupleBundle type trying to poke: $e")
        }
      case v: Int => poke_nested(signal.t0b, BigInt(v))
      case v: BigInt => poke_nested(signal.t0b, v)
      case _ => throw new Exception(s"Cannot poke value ${signal.t0b.getClass.getName}")
    }
    values(1) match {
      case v: IndexedSeq[_] =>
        signal.t1b match {
          case e: Aggregate => poke_nested(e, v)
          case e: TupleBundle => poke_nested(e, v)
          case e => throw new Exception(s"Not a Vec or TupleBundle type trying to poke: $e")
        }
      case v: Int => poke_nested(signal.t1b, BigInt(v))
      case v: BigInt => poke_nested(signal.t1b, v)
      case _ => throw new Exception(s"Cannot poke value ${signal.t1b.getClass.getName}")
    }
  }

  def poke_nested[TS](signal: Aggregate, values: IndexedSeq[TS]): Unit = {
    (extractElementBits(signal) zip values.reverse).foreach{ case (elem, value) =>
      value match {
        case v: IndexedSeq[_] =>
          elem match {
            case e: Aggregate => poke_nested(e, v)
            case e: TupleBundle => poke_nested(e, v)
            case e => throw new Exception(s"Not a Vec or TupleBundle type trying to poke: $e")
          }
        case v: Int => poke_nested(elem, v)
        case v: BigInt => poke_nested(elem, v)
        case _ => throw new Exception(s"Cannot poke value ${elem.getClass.getName}")
      }
    }
  }

  def poke_nested(signal: Data, value: Int): Unit = {
    (signal.asUInt(), BigInt(value))
  }
  def poke_nested(signal: Data, value: BigInt): Unit = {
    (signal.asUInt(), value)
  }

 /*
  def poke_nested_dispath(signal: Data, value: IndexedSeq[Data]): Unit = {
    signal match {
      case s: Aggregate => poke_nested(s, value)
      case s: TupleBundle => poke_nested(s, value)
      case _ => throw new Exception(s"Not a Vec or TupleBundle type trying to poke: $e")
    }
  }
  */

  private def extractElementBits(signal: Data): IndexedSeq[Element] = {
    signal match {
      case elt: Aggregate => elt.getElements.toIndexedSeq flatMap {extractElementBits(_)}
      case elt: Element => IndexedSeq(elt)
      case elt => throw new Exception(s"Cannot extractElementBits for type ${elt.getClass.getName}")
    }
  }
}

class Map2STupleUnitTester(c: Map2S) extends NestedPeekPokeTester(c) {
  for(i <- -10 to 10 by 1) {
    for(j <- 0 to 3 by 1) {
      poke_nested(c.in0, (0 to 3).map(j => BigInt(i*j)))
      poke_nested(c.in1, (0 to 3).map(j => BigInt(i*j+17)))
      //println(s"in: ${peek(c.io.in)}")
      //println(s"out: ${peek(c.io.out)}")
      //expect(c.out, (0 to 3).map(j => Array(i*j, i*j+17)))
    }
  }
}

class Map2STester extends ChiselFlatSpec {
  "MapS" should "take abs of four ints per clock correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Map2S(4, new AtomTuple(STInt(8), STInt(8)))) {
      c => new Map2STupleUnitTester(c)
    } should be(true)
  }
}
