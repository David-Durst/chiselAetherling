package aetherling.modules

import aetherling.modules.helpers.{InitialDelayCounter, NullaryInterface, ValidInterface}
import aetherling.types._
import chisel3.{Data, MultiIOModule, _}
import chisel3.util.Counter

object Const{
  def apply[T <: Data](t: STTypeDefinition, rom: Vec[T], delay: Int, valid_up: Bool): (T, Bool) = {
    val enabled = if (delay == 0) true.B else {
      val delay_counter = Module(new InitialDelayCounter(delay))
      delay_counter.valid_up := valid_up
      delay_counter.valid_down
    }

    val (counter_val, _) = Counter(enabled, rom.length)
    printf("counter_val: %d\n", counter_val)
    (rom(counter_val), enabled)
  }

  def make_vec[T <: Data](elts: T*): Vec[T] = VecInit(elts)
}
