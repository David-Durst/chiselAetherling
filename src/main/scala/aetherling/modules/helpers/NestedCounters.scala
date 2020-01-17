package aetherling.modules.helpers

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

class NestedCounters(t: STTypeDefinition, has_cur_valid: Boolean,
                      valid_down_when_ce_disabled: Boolean) extends MultiIOModule {
  val CE = IO(Input(Bool()))
  val valid = IO(Output(Bool()))
  val last = IO(Output(Bool()))

  val inner_nested_counter = Module(new _NestedCounters(t, valid_down_when_ce_disabled))
  inner_nested_counter.CE := CE
  valid := inner_nested_counter.valid
  last := inner_nested_counter.last

  if (has_cur_valid) {
    val (cur_valid_value, _) = Counter(CE && inner_nested_counter.valid, t.validClocks())
    val cur_valid = IO(Output(UInt(cur_valid_value.getWidth.W)))
    cur_valid := cur_valid_value
  }
}

class _NestedCounters(t: STTypeDefinition, valid_down_when_ce_disabled: Boolean) extends MultiIOModule {
  val CE = IO(Input(Bool()))
  val valid = IO(Output(Bool()))
  val last = IO(Output(Bool()))


  if (t.isInstanceOf[TSeq]) {
    val t_tseq = t.asInstanceOf[TSeq]
    val inner_counter = Module(new _NestedCounters(t_tseq.t, valid_down_when_ce_disabled))
    // wrap is when 0 after being n-1, last is when currently n-1
    val (outer_counter_value, _) = Counter(CE && inner_counter.last, t_tseq.n + t_tseq.i)

    last := (outer_counter_value === (t_tseq.n + t_tseq.i - 1).U) && inner_counter.last
    valid := (outer_counter_value < t_tseq.n.U) && inner_counter.valid
  }
  else if (t.isNested()) {

  }


}

