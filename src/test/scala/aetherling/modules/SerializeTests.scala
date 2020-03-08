package aetherling.modules

import aetherling.modules.helpers.NestedPeekPokeTester
import aetherling.types.{STInt, TSeq}
import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, PeekPokeTester}

class SerializeBasicTester(c: Serialize) extends NestedPeekPokeTester(c) {
  poke_nested(c.valid_up, true)
  for(i <- 0 to 3) {
    printf(s"clk: $i\n")
    if (i == 0)
      poke_nested(c.I, Array(BigInt(0), BigInt(1), BigInt(2)))
    else
      poke_nested(c.I, Array(BigInt(4), BigInt(4), BigInt(4)))
    if (i > 0) {
      expect_nested(c.O.asUInt(), i-1)
    }
    step(1)
  }
}

class SerializeTSeqTester(c: Serialize) extends NestedPeekPokeTester(c) {
  poke_nested(c.valid_up, true)
  for(i <- 0 to 9) {
    printf(s"clk: $i\n")
    if (i < 3)
      poke_nested(c.I, Array(BigInt(i), BigInt(i+3), BigInt(i+6)))
    else
      poke_nested(c.I, Array(BigInt(30), BigInt(30), BigInt(30)))
    if (i > 0) {
      expect_nested(c.O.asUInt(), i-1)
    }
    step(1)
  }
}

class SerializeTester extends ChiselFlatSpec {
  "Serialize" should "serialize three numbers correctly" in {
    iotesters.Driver.execute(Array(), () => new Serialize(3, 0, new STInt(8))) {
      c => new SerializeBasicTester(c)
    } should be(true)
  }

  "Serialize" should "serialize three TSeq(3,0,IntT) correctly" in {
    iotesters.Driver.execute(Array(), () => new Serialize(3, 0, TSeq(3, 0, new STInt(8)))) {
      c => new SerializeTSeqTester(c)
    } should be(true)
  }

}
