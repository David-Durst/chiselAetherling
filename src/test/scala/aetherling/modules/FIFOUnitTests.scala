package aetherling.modules

import aetherling.modules.helpers.NestedPeekPokeTester
import aetherling.types.STInt
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class FIFO1UnitTester(c: FIFO) extends NestedPeekPokeTester(c) {
  for(i <- 1 to 40 by 3) {
    poke_nested(c.I, i)
    poke_nested(c.valid_up, true)
    if (i > 1) {
      expect_nested(c.O, i - 3)
      expect_nested(c.valid_down, true)
    }
    else {
      expect_nested(c.valid_down, false)
    }
    step(1)
  }
}

class FIFO4UnitTester(c: FIFO) extends NestedPeekPokeTester(c) {
  for(i <- 1 to 41 by 2) {
    poke_nested(c.I, i)
    poke_nested(c.valid_up, true)
    peek_unary_module(c)
    if (i > 7) {
      expect_nested(c.O, i - 2*4)
      expect_nested(c.valid_down, true)
    }
    else {
      expect_nested(c.valid_down, false)
    }
    step(1)
  }
}

class FIFOTester extends ChiselFlatSpec {
  "FIFO" should "delay by 1 clock correctly for ints" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new FIFO(STInt(8), 1)) {
      c => new FIFO1UnitTester(c)
    } should be(true)
  }

  "FIFO" should "delay by 4 clocks correctly for ints" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new FIFO(STInt(8), 4)) {
      c => new FIFO4UnitTester(c)
    } should be(true)
  }
}
