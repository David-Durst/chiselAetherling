package aetherling.modules

import java.io.File

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class AddUnitTester(c: Add) extends PeekPokeTester(c) {
  for(i <- 1 to 40 by 3) {
    for (j <- 1 to 40 by 7) {
      poke(c.io.in0, i)
      poke(c.io.in1, j)
      expect(c.io.out, i + j)
    }
  }
}

class AbsUnitTester(c: Abs) extends PeekPokeTester(c) {
  for(i <- -10 to 10 by 1) {
    poke(c.io.in, i)
    //println(s"in: ${peek(c.io.in)}")
    //println(s"out: ${peek(c.io.out)}")
    expect(c.io.out, scala.math.abs(i))
  }
}

class ArithmeticTester extends ChiselFlatSpec {
  "Add" should "add two ints correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Add(8)) {
      c => new AddUnitTester(c)
    } should be(true)
  }

  "Abs" should "take absolute value of an int correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Abs(8)) {
      c => new AbsUnitTester(c)
    } should be(true)
  }
}
