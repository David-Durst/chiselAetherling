package aetherling.modules.helpers

import aetherling.types._
import chisel3._
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec}
import math.max
import chisel3.experimental.DataMirror

// inner most IndexedSeq are multiple values passed to
class RAM_STTester(c: RAM_ST, data_per_elem_per_clk: IndexedSeq[IndexedSeq[IndexedSeq[_]]], clocks_per_elem: Int,
                   num_elements: Int) extends NestedPeekPokeTester(c) {
  poke(c.WE, true.B)
  poke(c.RE, true.B)
  for (elem <- 0 to num_elements - 1) {
    for (clk_of_elm <- 0 to clocks_per_elem - 1) {
      poke(c.WADDR, elem.U)
      poke(c.RADDR, (max(elem - 1, 0)).U)
      poke_nested(c.WDATA, data_per_elem_per_clk(elem)(clk_of_elm))
      step(1)
      peek_any_module(c)
      if (elem > 0) {
        expect_nested(c.RDATA, data_per_elem_per_clk(elem - 1)(clk_of_elm))
      }
    }
  }
}

class RAM_STTests extends ChiselFlatSpec {
  "RAM_ST" should "store 2 ints correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new RAM_ST(SSeq(2, STInt(8)), 2)) {
      c => new RAM_STTester(c, Vector(Vector(Vector(1, 2)), Vector(Vector(3, 4))), 1, 2)
    } should be(true)
  }

  "RAM_ST" should "store 1 int per clock for 2 clocks correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new RAM_ST(TSeq(2, 0, SSeq(1, STInt(8))), 3)) {
      c => new RAM_STTester(c, Vector(Vector(Vector(1), Vector(2)), Vector(Vector(3), Vector(4)),
        Vector(Vector(5), Vector(6))), 2, 3)
    } should be(true)
  }

}


