package aetherling.modules.higherorder

import aetherling.modules.helpers.{BinaryInterface, NestedPeekPokeTester, ValidInterface}
import aetherling.modules.{Abs, Add, AtomTuple}
import aetherling.types.{STInt, TupleBundle}
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester, Pokeable}

class Map2STupleUnitTester(c: Map2S) extends NestedPeekPokeTester(c) {
  for(i <- 0 to 10 by 1) {
    poke_nested(c.in0, (0 to 3).map(j => BigInt(i*j)))
    poke_nested(c.in1, (0 to 3).map(j => BigInt(i*j+17)))
    println(s"in0: ${peek_str(c.in0)}")
    println(s"in1: ${peek_str(c.in1)}")
    println(s"out: ${peek_str(c.out)}")
    expect_nested(c.out, (0 to 3).map(j => Array(i*j, i*j+17)))
  }
}

class TestAdder extends MultiIOModule with BinaryInterface with ValidInterface {
  override val in0: Data = IO(Input(STInt(8).chiselRepr()))
  override val in1: Data = IO(Input(STInt(8).chiselRepr()))
  override val out: Data = IO(Output(STInt(8).chiselRepr()))

  val tupler = Module(new AtomTuple(STInt(8), STInt(8)))
  val inner_adder = Module(new Add(STInt(8)))

  tupler.in0 := in0
  tupler.in1 := in1
  inner_adder.in := tupler.out
  out := inner_adder.out
  tupler.valid_up := valid_up
  inner_adder.valid_up := tupler.valid_down
  valid_down := inner_adder.valid_down
}

class Map2SAddUnitTester(c: Map2S) extends NestedPeekPokeTester(c) {
  for(i <- 0 to 10 by 1) {
    poke_nested(c.in0, (0 to 3).map(j => BigInt(i*j)))
    poke_nested(c.in1, (0 to 3).map(j => BigInt(i*j+17)))
    expect_nested(c.out, (0 to 3).map(j => i*j + i*j+17))
  }
}

class Map2SAddBigUnitTester(c: Map2S) extends NestedPeekPokeTester(c) {
  for(i <- 0 to 10 by 1) {
    poke_nested(c.in0, (0 to 200).map(j => BigInt(i+j)))
    poke_nested(c.in1, (0 to 200).map(j => BigInt(i)))
    expect_nested(c.out, (0 to 200).map(j => i+j + i))
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
