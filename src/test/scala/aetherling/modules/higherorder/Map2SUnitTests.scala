package aetherling.modules.higherorder

import aetherling.modules.helpers.{BinaryInterface, NestedPeekPokeTester, ValidInterface}
import aetherling.modules.{Abs, Add, AtomTuple}
import aetherling.types.{STInt, TupleBundle}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester, Pokeable}

class Map2STupleUnitTester(c: Map2S) extends NestedPeekPokeTester(c) {
  for(i <- 0 to 10 by 1) {
    poke_nested(c.I0, (0 to 3).map(j => BigInt(i*j)))
    poke_nested(c.I1, (0 to 3).map(j => BigInt(i*j+17)))
    println(s"in0: ${peek_str(c.I0)}")
    println(s"in1: ${peek_str(c.I1)}")
    println(s"out: ${peek_str(c.O)}")
    expect_nested(c.O, (0 to 3).map(j => IndexedSeq(i*j, i*j+17)))
  }
}

class TestAdder extends MultiIOModule with BinaryInterface with ValidInterface {
  override val I0: Data = IO(Input(STInt(8).chiselRepr()))
  override val I1: Data = IO(Input(STInt(8).chiselRepr()))
  override val O: Data = IO(Output(STInt(8).chiselRepr()))

  val tupler = Module(new AtomTuple(STInt(8), STInt(8)))
  val inner_adder = Module(new Add(STInt(8)))

  tupler.I0 := I0
  tupler.I1 := I1
  inner_adder.I := tupler.O
  O := inner_adder.O
  tupler.valid_up := valid_up
  inner_adder.valid_up := tupler.valid_down
  valid_down := inner_adder.valid_down
}

class Map2SAddUnitTester(c: Map2S) extends NestedPeekPokeTester(c) {
  for(i <- 0 to 10 by 1) {
    poke_nested(c.I0, (0 to 3).map(j => BigInt(i*j)))
    poke_nested(c.I1, (0 to 3).map(j => BigInt(i*j+17)))
    expect_nested(c.O, (0 to 3).map(j => i*j + i*j+17))
  }
}

class Map2SAddBigUnitTester(c: Map2S) extends NestedPeekPokeTester(c) {
  for(i <- 0 to 10 by 1) {
    poke_nested(c.I0, (0 to 200).map(j => BigInt(i+j)))
    poke_nested(c.I1, (0 to 200).map(j => BigInt(i)))
    expect_nested(c.O, (0 to 200).map(j => i+j + i))
  }
}

class Map2STester extends ChiselFlatSpec {
  "Map2S" should "take tuple 4 pairs of ints per clock" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Map2S(4, new AtomTuple(STInt(8), STInt(8)))) {
      c => new Map2STupleUnitTester(c)
    } should be(true)
  }

  "Map2S" should "add 4 pairs of ints per clock" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Map2S(4, new TestAdder())) {
      c => new Map2SAddUnitTester(c)
    } should be(true)
  }

  "Map2S" should "add 201 pairs of ints per clock" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Map2S(201, new TestAdder())) {
      c => new Map2SAddUnitTester(c)
    } should be(true)
  }
}
