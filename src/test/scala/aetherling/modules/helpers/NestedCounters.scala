package aetherling.modules.helpers

import aetherling.modules.{Abs, Add, AddUnitTester}
import aetherling.types._
import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec}

class NestedCounterTester(c: NestedCounters, validClocks: Vector[Boolean]) extends NestedPeekPokeTester(c) {
  poke(c.CE, true)
  for(i <- 0 to c.t.time() - 1) {
    println(s"clk: ${i}")
    //println(s"in: ${peek(c.io.in)}")
    //println(s"out: ${peek(c.io.out)}")
    expect(c.valid, validClocks(i))
    step(1)
  }
}

class NestedCounterWithNumValidTester(c: NestedCountersWithNumValid, validClocks: Vector[Boolean]) extends NestedPeekPokeTester(c) {
  var numValid = 0
  poke(c.CE, true)
  for(i <- 0 to c.t.time() - 1) {
    println(s"clk: ${i}")
    //println(s"in: ${peek(c.io.in)}")
    //println(s"out: ${peek(c.io.out)}")
    expect(c.valid, validClocks(i))
    expect(c.cur_valid, numValid)
    if(validClocks(i) == true) {
      numValid += 1
    }
    step(1)
  }
}

class NestedCounterTests extends ChiselFlatSpec {
  "NestedCounters" should "emit valid always for atoms" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new NestedCounters(STInt(8), true)) {
      c => new NestedCounterTester(c, Vector(true))
    } should be(true)
  }

  "NestedCounters" should "emit valid always for SSeq" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new NestedCounters(SSeq(3, STInt(8)), true)) {
      c => new NestedCounterTester(c, Vector(true))
    } should be(true)
  }

  "NestedCounters" should "emit valid always for TSeq where always valid" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new NestedCounters(TSeq(3, 0, STInt(8)), true)) {
      c => new NestedCounterTester(c, Vector(true, true, true))
    } should be(true)
  }

  "NestedCounters" should "emit valid and invalid for TSeq where sometimes valid" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new NestedCounters(TSeq(3, 2, STInt(8)), true)) {
      c => new NestedCounterTester(c, Vector(true, true, true, false, false))
    } should be(true)
  }

  "NestedCounters" should "emit valid and invalid for nested TSeq where sometimes valid" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new NestedCounters(TSeq(1, 1, TSeq(2, 1, STInt(8))), true)) {
      c => new NestedCounterTester(c, Vector(true, true, false, false, false, false))
    } should be(true)
  }

  "NestedCountersWithNumValid" should "emit correct counts for nested TSeq where sometimes valid" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new NestedCounters(TSeq(2, 1, TSeq(1, 1, STInt(8))), true)) {
      c => new NestedCounterTester(c, Vector(true, false, true, false, false, false))
    } should be(true)
  }
}
