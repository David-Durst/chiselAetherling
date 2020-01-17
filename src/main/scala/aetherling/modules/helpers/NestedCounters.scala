package aetherling.modules.helpers

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

/**
  * Create a counter that emits value on each valid element of a nested type and tracks the index of the valid element
  * @param t - the type
  * @param valid_down_when_ce_disabled - should this emit valid when on a valid element but CE is enabled
  */
class NestedCountersWithNumValid(val t: STTypeDefinition, has_cur_valid: Boolean,
                      valid_down_when_ce_disabled: Boolean) extends MultiIOModule {
  val CE = IO(Input(Bool()))
  val valid = IO(Output(Bool()))
  // is this the last element of the type
  val last = IO(Output(Bool()))

  private val inner_nested_counter = Module(new NestedCounters(t, valid_down_when_ce_disabled))
  inner_nested_counter.CE := CE
  valid := inner_nested_counter.valid
  last := inner_nested_counter.last

  private val (cur_valid_value, _) = Counter(CE && inner_nested_counter.valid, t.validClocks())
  val cur_valid = IO(Output(UInt(cur_valid_value.getWidth.W)))
  cur_valid := cur_valid_value
}

/**
  * Create a counter that emits value on each valid element of a nested type
  * @param t - the type
  * @param valid_down_when_ce_disabled - should this emit valid when on a valid element but CE is enabled
  */
class NestedCounters(val t: STTypeDefinition, valid_down_when_ce_disabled: Boolean) extends MultiIOModule {
  val CE = IO(Input(Bool()))
  val valid = IO(Output(Bool()))
  val last = IO(Output(Bool()))


  if (t.isInstanceOf[TSeq[_]]) {
    val t_tseq = t.asInstanceOf[TSeq[STTypeDefinition]]
    val inner_counter = Module(new NestedCounters(t_tseq.t, valid_down_when_ce_disabled))
    // wrap is when 0 after being n-1, last is when currently n-1
    val (outer_counter_value, _) = Counter(CE && inner_counter.last, t_tseq.n + t_tseq.i)

    last := (outer_counter_value === (t_tseq.n + t_tseq.i - 1).U) && inner_counter.last
    valid := (outer_counter_value < t_tseq.n.U) && inner_counter.valid
    inner_counter.CE := CE
  }
  else if (t.isInstanceOf[NestedSTType[_]]) {
    val t_nested = t.asInstanceOf[NestedSTType[STTypeDefinition]]
    val inner_counter = Module(new NestedCounters(t_nested.t, valid_down_when_ce_disabled))

    last := inner_counter.last
    valid := inner_counter.valid
    inner_counter.CE := CE
  }
  else {
    last := true.B
    if (valid_down_when_ce_disabled) {
      valid := true.B
    }
    else {
      valid := CE
    }
  }
}

