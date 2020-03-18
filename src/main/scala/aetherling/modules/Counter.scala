package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

/**
  * Create a counter that emits n values from 0 to (n-1)*incr_amount inclusive as (SSeq n int_type)
  * @param n - number of counted outputs
  * @param incr_amount - amount each element should increase by
  * @param int_type - the type of int for the counter
  * @param delay - does nothing
  */
class Counter_S(val n: Int, val incr_amount: Int, val int_type: STInt, val delay: Int)
  extends MultiIOModule with NullaryInterface with ValidInterface {
  override val O = IO(Output(Vec(n, int_type.chiselRepr())))
  val values = for (i <- 0 to n-1) yield Counter_Helpers.int_to_chisel_int(i*incr_amount, int_type)

  O := VecInit(values)
  valid_down := true.B
}

/**
  * Create a counter that emits n values from 0 to (n-1)*incr_amount inclusive as (TSeq n i int_type)
  * @param n - number of counted outputs
  * @param i - number of invalids
  * @param incr_amount - amount each element should increase by
  * @param int_type - the type of int for the counter
  * @param delay - does nothing
  */
class Counter_T(val n: Int, val i: Int, val incr_amount: Int, val int_type: STInt, val delay: Int)
  extends MultiIOModule with NullaryInterface with ValidInterface {
  override val O = IO(Output(int_type.chiselRepr()))

  val enabled = if (delay == 0) true.B else {
    val delay_counter = Module(new InitialDelayCounter(delay))
    // delay handles when to start this counter. don't listen to valid signal.
    // just handle valid signal to make interfaces uniform for Haskell compiler.
    delay_counter.valid_up := true.B
    delay_counter.valid_down
  }

  val counter_value = RegInit(int_type.chiselRepr(), Counter_Helpers.int_to_chisel_int(0, int_type))

  if (int_type.signed) {
    val wrap = counter_value.asInstanceOf[SInt] === (n+i-1).S
    when (enabled && wrap) { counter_value.asInstanceOf[SInt] := 0.S }
    .elsewhen(enabled) { counter_value.asInstanceOf[SInt] := counter_value.asInstanceOf[SInt] +
      incr_amount.S(int_type.width.W)}
  } else {
    val wrap = counter_value.asInstanceOf[UInt] === (n+i-1).U
    when (enabled && wrap) { counter_value.asInstanceOf[UInt] := 0.U }
    .elsewhen(enabled) {counter_value.asUInt := counter_value.asUInt + incr_amount.U(int_type.width.W)}
  }

  O := counter_value
  valid_down := true.B
}


/**
  * Create a counter that emits n values from 0 to (n-1)*incr_amount inclusive as (TSeq no io (SSeq ni int_type))
  * @param no - number of counted outputs for TSeq
  * @param io - number of invalids
  * @param ni - number of counted outputs for SSeq
  * @param incr_amount - amount each element should increase by
  * @param int_type - the type of int for the counter
  * @param delay - does nothing
  */
class Counter_TS(val no: Int, val io: Int, val ni: Int, val incr_amount: Int, val int_type: STInt, val delay: Int)
  extends MultiIOModule with NullaryInterface with ValidInterface {
  override val O = IO(Output(Vec(ni, int_type.chiselRepr())))

  val counter_t = Module(new Counter_T(no, io, incr_amount*ni, int_type, delay))
  val counter_s = Module(new Counter_S(ni, incr_amount, int_type, delay))

  if (int_type.signed) {
    for (i <- 0 to ni) {
      O := counter_s.O(i).asInstanceOf[SInt] + counter_t.O.asInstanceOf[SInt]
    }
  } else {
    for (i <- 0 to ni) {
      O := counter_s.O(i).asInstanceOf[UInt] + counter_t.O.asInstanceOf[UInt]
    }
  }

  valid_down := counter_t.valid_down
}

/**
  * Create a counter that emits n values from 0 to (n-1)*incr_amount inclusive as (TSeq n0 i0 (TSeq n1 i 1 (... int_type))
  * @param ns - number of counted outputs
  * @param is - number of invalids
  * @param incr_amount - amount each element should increase by
  * @param int_type - the type of int for the counter
  * @param delay - does nothing
  */
class Counter_TN(val ns: IndexedSeq[Int], val is: IndexedSeq[Int], val incr_amount: Int, val int_type: STInt, val delay: Int)
  extends MultiIOModule with NullaryInterface with ValidInterface {
  override val O = IO(Output(int_type.chiselRepr()))

  var types = int_type.asInstanceOf[STTypeDefinition]
  for (i <- ns.length - 1 to 0 by -1) {
    types = TSeq(ns(i), is(i), types)
  }
  val nested_counters = Module(new NestedCounters(types, false))


  val enabled = if (delay == 0) true.B else {
    val delay_counter = Module(new InitialDelayCounter(delay))
    // delay handles when to start this counter. don't listen to valid signal.
    // just handle valid signal to make interfaces uniform for Haskell compiler.
    delay_counter.valid_up := true.B
    delay_counter.valid_down
  }

  nested_counters.CE := enabled

  val counter_value = RegInit(int_type.chiselRepr(), Counter_Helpers.int_to_chisel_int(0, int_type))

  if (int_type.signed) {
    when (enabled && nested_counters.last) { counter_value.asInstanceOf[SInt] := 0.S }
      .elsewhen(enabled) { counter_value.asInstanceOf[SInt] := counter_value.asInstanceOf[SInt] +
        incr_amount.S(int_type.width.W)}
  } else {
    when (enabled && nested_counters.last) { counter_value.asInstanceOf[UInt] := 0.U }
      .elsewhen(enabled) {counter_value.asUInt := counter_value.asUInt + incr_amount.U(int_type.width.W)}
  }


  O := counter_value
  valid_down := enabled
}

object Counter_Helpers {
  def int_to_chisel_int(v: Int, int_type: STInt): Data = {
    if (int_type.signed) {
      v.S(int_type.width.W)
    } else {
      v.U(int_type.width.W)
    }
  }
}
