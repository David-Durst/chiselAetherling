package aetherling.modules.higherorder

import aetherling.modules.Abs
import aetherling.types.STInt
import chisel3.{iotesters, _}
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class MapTAbsUnitTester(c: MapT) extends PeekPokeTester(c) {
  for(i <- -10 to 10 by 1) {
    poke(c.in.asInstanceOf[UInt], BigInt(i))
    //println(s"in: ${peek(c.io.in)}")
    //println(s"out: ${peek(c.io.out)}")
    expect(c.out.asInstanceOf[UInt], scala.math.abs(i))
  }
}

class MapTTester extends ChiselFlatSpec {
  "MapT" should "take abs of four ints per clock correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new MapT(new Abs(STInt(8)))) {
      c => new MapTAbsUnitTester(c)
    } should be(true)
  }
}
