package aetherling.modules.higherorder

import aetherling.modules.helpers.NestedPeekPokeTester
import aetherling.modules.{Abs, AtomTuple}
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

class Map2STester extends ChiselFlatSpec {
  "Map2S" should "take tuple 4 pairs of ints per clock" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Map2S(4, new AtomTuple(STInt(8), STInt(8)))) {
      c => new Map2STupleUnitTester(c)
    } should be(true)
  }
}
