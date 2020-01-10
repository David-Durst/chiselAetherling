package aetherling.modules.higherorder

import aetherling.modules.AtomTuple
import aetherling.modules.helpers.NestedPeekPokeTester
import aetherling.types.{STInt, TupleBundle}
import chisel3._
import chisel3.iotesters.ChiselFlatSpec

class Map2TTupleUnitTester(c: Map2T) extends NestedPeekPokeTester(c) {
  for(i <- 0 to 10 by 1) {
    poke_nested(c.in0, i)
    poke_nested(c.in1, i+17)
    poke_nested(c.valid_up, true)
    peek_binary_module(c)
    expect_nested(c.out, Array(i, i+17))
    expect_nested(c.valid_down, true)
    step(1)
  }
}

class Map2TTester extends ChiselFlatSpec {
  "Map2T" should "take tuple 1 pair of ints per clock" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Map2T(new AtomTuple(STInt(8), STInt(8)))) {
      c => new Map2TTupleUnitTester(c)
    } should be(true)
  }
}
