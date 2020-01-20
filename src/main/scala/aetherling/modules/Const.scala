package aetherling.modules

import aetherling.modules.helpers.{ChiselValueOperators, InitialDelayCounter, NullaryInterface, ValidInterface}
import aetherling.types._
import chisel3.{Data, MultiIOModule, _}
import chisel3.util.Counter

object Const{
  def apply(t: STTypeDefinition, rom: Vec[Data], delay: Int, valid_up: Bool): (Data, Bool) = {
    val enabled = if (delay == 0) true.B else {
      val delay_counter = Module(new InitialDelayCounter(delay))
      // delay handles when to start this counter. don't listen to valid signal.
      // just handle valid signal to make interfaces uniform for Haskell compiler.
      delay_counter.valid_up := true.B
      delay_counter.valid_down
    }

    val (counter_val, _) = Counter(enabled, rom.length)

    // nest the types correctly
    val correct_type_wire = Wire(t.chiselRepr())
    val rom_output = rom(counter_val)
    val rom_flattened = ChiselValueOperators.flattenChiselValue(rom_output)
    val O_flattened = ChiselValueOperators.flattenChiselValue(correct_type_wire)
    rom_flattened zip O_flattened map { case(i_elem, o_elem) => o_elem := i_elem }

    (correct_type_wire, enabled)
  }

  def make_vec[T <: Data](elts: T*): Vec[T] = VecInit(elts)
}
