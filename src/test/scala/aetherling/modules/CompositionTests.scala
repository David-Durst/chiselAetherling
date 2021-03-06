package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.modules._
import aetherling.types._
import chisel3._
import chisel3.iotesters.ChiselFlatSpec

class PipelinedAdderTest(c: PipelinedAdder) extends NestedPeekPokeTester(c) {
  poke_nested(c.valid_up, true.B)
  for(i <- 0 to 10 by 1) {
    poke_nested(c.I, (0 to 200).map(j => BigInt(i+j)))
    println(s"Cycle $i")
    peek_unary_module(c)
    if (i >= 4) {
      expect_nested(c.O, (0 to 200).map(j => (i - 4) + j + 5))
    }
    step(1)
  }
}
class PipelinedAdder extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I: Data = IO(Input(SSeq(200, STInt(8)).chiselRepr()))
  override val O: Data = IO(Output(SSeq(200, STInt(8)).chiselRepr()))

  val fifo0 = Module(new FIFO(SSeq(200, STInt(8)), 1))
  val tupler = Module(new Map2S(200, new AtomTuple(STInt(8), STInt(8))))
  val inner_adder = Module(new MapS(200, new Add(STInt(8))))
  val fifo1 = Module(new FIFO(SSeq(200, STInt(8)), 1))
  val fifo2 = Module(new FIFO(SSeq(200, STInt(8)), 1))
  val fifo3 = Module(new FIFO(SSeq(200, STInt(8)), 1))

  fifo0.I := I
  tupler.I0 := fifo0.O
  for (i <- 0 to 199) {
    tupler.I1(i) := 5.U
  }
  inner_adder.I := tupler.O
  /*
  printf("fifo0_out: %d\n", fifo0.out.asInstanceOf[Vec[UInt]](1))
  printf("adder_out: %d\n", inner_adder.out(1).asUInt())
  printf("fifo1_out: %d\n", fifo1.out.asInstanceOf[Vec[UInt]](1))
  printf("fifo2_out: %d\n", fifo2.out.asInstanceOf[Vec[UInt]](1))
  printf("fifo3_out: %d\n", fifo3.out.asInstanceOf[Vec[UInt]](1))
   */
  fifo1.I := inner_adder.O
  fifo2.I := fifo1.O
  fifo3.I := fifo2.O
  O := fifo3.O
  fifo0.valid_up := valid_up
  tupler.valid_up := fifo0.valid_up
  inner_adder.valid_up := tupler.valid_down
  fifo1.valid_up := inner_adder.valid_down
  fifo2.valid_up := fifo1.valid_down
  fifo3.valid_up := fifo2.valid_down
  valid_down := fifo3.valid_down
}
class CompositionTester extends ChiselFlatSpec {
  "PipelinedAdder" should "add 200 pairs of ints per clock with 4 clock delay" in {
    iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new PipelinedAdder) {
      c => new PipelinedAdderTest(c)
    } should be(true)
  }
}
