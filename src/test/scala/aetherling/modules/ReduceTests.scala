package aetherling.modules

import aetherling.modules.helpers.NestedPeekPokeTester
import aetherling.modules.higherorder.{MapTNoValid, ReduceT}
import aetherling.types.{STInt, TSeq}
import chisel3.iotesters
import chisel3.iotesters.ChiselFlatSpec

class ReduceBasicTester(c: ReduceT) extends NestedPeekPokeTester(c) {
  poke_nested(c.valid_up, false)
  step(3)
  poke_nested(c.valid_up, true)
  for(i <- 0 to 4) {
    printf(s"clk: $i\n")
    poke_nested(c.I, BigInt(i))
    peek_any_module(c)
    if (i < 4) {
      expect_nested(c.valid_down, false)
    }
    else {
      expect_nested(c.valid_down, true)
    }
    if (i == 4) {
      expect_nested(c.O.asUInt(), 0+1+2)
    }
    step(1)
  }
}

class ReduceTSeqTester(c: ReduceT) extends NestedPeekPokeTester(c) {
  poke_nested(c.valid_up, true)
  for(i <- 0 to 11) {
    printf(s"clk: $i\n")
    if (i % 3 == 0)
      poke_nested(c.I, BigInt(i))
    else
      poke_nested(c.I, BigInt(30))
    peek_any_module(c)
    if (i < 8) {
      expect_nested(c.valid_down, false)
    }
    else {
      expect_nested(c.valid_down, true)
    }
    if (i == 8) {
      expect_nested(c.O.asUInt(), 0+3+6)
    }
    step(1)
  }
}

class ReduceTSeqRepeatedTester(c: ReduceT) extends NestedPeekPokeTester(c) {
  poke_nested(c.valid_up, false)
  step(3)
  poke_nested(c.valid_up, true)
  for(i <- 0 to 17) {
    printf(s"clk: $i\n")
    if (i % 3 == 0)
      poke_nested(c.I, BigInt(i))
    else
      poke_nested(c.I, BigInt(30))
    peek_any_module(c)
    if (i < 8) {
      expect_nested(c.valid_down, false)
    }
    else {
      expect_nested(c.valid_down, true)
    }
    if (i == 8) {
      expect_nested(c.O.asUInt(), 0+3+6)
    }
    if (i == 17) {
      expect_nested(c.O.asUInt(), 9+12+15)
    }
    step(1)
  }
}

class ReduceTester extends ChiselFlatSpec {
  "Reduce" should "sum three numbers correctly" in {
    iotesters.Driver.execute(Array(), () => new ReduceT(3, 0, new AddNoValid(STInt(8, false)), new STInt(8))) {
      c => new ReduceBasicTester(c)
    } should be(true)
  }

  "Reduce" should "sum TSeq(1,2,IntT) correctly" in {
    iotesters.Driver.execute(Array(), () =>
      new ReduceT(3, 0, new MapTNoValid(new AddNoValid((STInt(8, false)))), TSeq(1, 2, new STInt(8)))) {
      c => new ReduceTSeqTester(c)
    } should be(true)
  }

  "Reduce" should "sum repeated TSeq(1,2,IntT) correctly" in {
    iotesters.Driver.execute(Array(), () =>
      new ReduceT(3, 0, new MapTNoValid(new AddNoValid((STInt(8, false)))), TSeq(1, 2, new STInt(8)))) {
      c => new ReduceTSeqRepeatedTester(c)
    } should be(true)
  }

}
