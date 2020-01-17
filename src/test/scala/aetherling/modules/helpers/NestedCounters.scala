package aetherling.modules.helpers

import aetherling.modules.{Abs, Add, AddUnitTester}
import aetherling.types.STInt
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class NestedCounterTester(c: NestedCounters, validClocks: Vector[Boolean], countValids: Boolean) extends NestedPeekPokeTester(c) {
  var num_valids = 0
  for(i <- 0 to c.t.time() - 1) {
    println(s"clk: ${i}")
    //println(s"in: ${peek(c.io.in)}")
    //println(s"out: ${peek(c.io.out)}")
    expect(c.valid)
    expect(c.O, scala.math.abs(i))
    step(1)
    num_valids += 1
  }
}

class NestedCounterTests extends ChiselFlatSpec {
  "Add" should "add two ints correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Add(new STInt(8))) {
      c => new AddUnitTester(c)
    } should be(true)
  }

  "Abs" should "take absolute value of an int correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Abs(STInt(8))) {
      c => new AbsUnitTester(c)
    } should be(true)
  }
}
