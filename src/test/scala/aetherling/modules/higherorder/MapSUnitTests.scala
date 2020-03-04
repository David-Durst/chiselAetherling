package aetherling.modules.higherorder

import aetherling.modules.{Abs, AbsUnitTester, Add, AddUnitTester}
import aetherling.types.STInt
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}
import chisel3._

class MapSAbsUnitTester(c: MapS) extends PeekPokeTester(c) {
  for(i <- -10 to 10 by 1) {
    for(j <- 0 to 3 by 1) {
      poke(c.I(j).asInstanceOf[SInt], BigInt(i*j))
    }
    step(1)
    for(j <- 0 to 3 by 1) {
      expect(c.O(j).asInstanceOf[SInt], scala.math.abs(i*j))
    }
  }
}

class MapSTester extends ChiselFlatSpec {
  "MapS" should "take abs of four ints per clock correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new MapS(4, new Abs(STInt(8, signed = false)))) {
      c => new MapSAbsUnitTester(c)
    } should be(true)
  }
}
