package aetherling.modules

import aetherling.types.STInt
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class AtomTupleUnitTester(c: AtomTuple) extends PeekPokeTester(c) {
  for(i <- 1 to 40 by 3) {
    for (j <- 1 to 40 by 7) {
      poke(c.in0.asUInt(), BigInt(i))
      poke(c.in1.asUInt(), BigInt(j))
      expect(c.out, Array(BigInt(i), BigInt(j)))
    }
  }
}

class TupleTester extends ChiselFlatSpec {
  "AtomTuple" should "tuple two ints correctly" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new AtomTuple(STInt(8), STInt(8))) {
      c => new AtomTupleUnitTester(c)
    } should be(true)
  }
}
