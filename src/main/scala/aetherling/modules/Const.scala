package aetherling.modules

import aetherling.modules.helpers.{NullaryInterface, ValidInterface, InitialDelayCounter}
import aetherling.types._
import chisel3.MultiIOModule
import chisel3._
import chisel3.util.Counter

class Const[T](t: STTypeDefinition, data: Seq[Data], delay: Int)
  extends MultiIOModule with NullaryInterface with ValidInterface {
  override val O = IO(Output(t.chiselRepr()))

  val enabled = if (delay == 0) true.B else {
    val delay_counter = Module(new InitialDelayCounter(delay))
    delay_counter.valid_up := valid_up
    delay_counter.valid_down
  }

  val rom = VecInit(data)
  val r = rom(Counter(rom.length).value)
}
