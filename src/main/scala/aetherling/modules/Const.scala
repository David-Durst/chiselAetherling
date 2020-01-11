package aetherling.modules

import aetherling.modules.helpers.{NullaryInterface, ValidInterface, InitialDelayCounter}
import aetherling.types._
import chisel3.MultiIOModule
import chisel3._
import chisel3.util.Counter

object Const{
  def apply[T <: Data](t: STTypeDefinition, rom: Vec[T], delay: Int, valid_up: Bool): (T, Bool) = {
    val enabled = if (delay == 0) true.B else {
      val delay_counter = Module(new InitialDelayCounter(delay))
      delay_counter.valid_up := valid_up
      delay_counter.valid_down
    }

    (rom(Counter(rom.length).value), enabled)
  }
}
