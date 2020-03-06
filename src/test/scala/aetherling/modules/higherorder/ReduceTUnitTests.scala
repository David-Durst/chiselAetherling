package aetherling.modules.higherorder

import aetherling.modules.{Add, AddNoValid}
import aetherling.types.STInt
import aetherling.modules.helpers._
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}
import chisel3.{iotesters, _}

class ReduceTAddUnitTester(c: ReduceT) extends NestedPeekPokeTester(c) {
  poke(c.valid_up, true.B)
  var f_clk = 0
  for(i <- -2 to 3 by 1) {
    println(s"clk: $f_clk")

    poke(c.I.asInstanceOf[SInt], BigInt(i))
    //println(s"in: ${peek(c.io.in)}")
    //println(s"out: ${peek(c.io.out)}")
    if (i != 3) {
      expect(c.valid_down.asInstanceOf[Bool], false.B)
    }
    peek_any_module(c)
    step(1)
    if (i == 3) {
      expect(c.valid_down.asInstanceOf[Bool], true.B)
    }
    f_clk += 1
  }
  expect(c.O.asInstanceOf[SInt], 3)
}

class ReduceTTester extends ChiselFlatSpec {
  "ReduceT" should "add 6 ints" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new ReduceT(6, 0, new AddNoValid(STInt(8, signed = true)), STInt(8, true))) {
      c => new ReduceTAddUnitTester(c)
    } should be(true)
  }
}
