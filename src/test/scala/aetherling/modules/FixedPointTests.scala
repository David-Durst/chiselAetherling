package aetherling.modules

import aetherling.modules.helpers.NestedPeekPokeTester
import aetherling.modules.FixedPointExperiments
import aetherling.types.{STInt, TSeq}
import chisel3.iotesters
import chisel3.iotesters.ChiselFlatSpec

class FixedPointTests(c: FixedPointExperiments) extends NestedPeekPokeTester(c) {
  step(3)
  peek_any_module(c)
}

class FixedPointTester extends ChiselFlatSpec {
  "FixedPoint" should "show me the verilog" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new FixedPointExperiments()) {
      c => new FixedPointTests(c)
    } should be(true)
  }
}
