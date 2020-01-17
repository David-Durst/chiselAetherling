package aetherling.modules.helpers

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.{Counter, MuxLookup}

import math.max

class RAM_ST(t: STTypeDefinition, n: Int) extends MultiIOModule {
  val RE = IO(Input(Bool()))
  val RADDR = IO(Input(UInt(getRAMAddrWidth(n))))
  val RDATA = IO(Input(t.chiselRepr()))
  val WE = IO(Input(Bool()))
  val WADDR = IO(Input(UInt(getRAMAddrWidth(n))))
  val WDATA = IO(Input(t.chiselRepr()))


  val (read_counter_value, _) = Counter(RE, t.time())
  val (write_counter_value, _) = Counter(WE, t.time())
  val write_elem_counter = Module(new NestedCountersWithNumValid(t, false))
  val read_elem_counter = Module(new NestedCountersWithNumValid(t, false))
  write_elem_counter.CE := WE
  read_elem_counter.CE := RE

  val rams = for (_ <- 0 to n-1) yield SyncReadMem(t.validClocks(), t.chiselRepr())

  val ramOutWires = (for (_ <- 0 to n-1) yield Wire(t.chiselRepr())).toArray
  for (i <- 0 to (n-1)) {
    when(i.U === WADDR && write_elem_counter.valid) { rams(i).write(WADDR, WDATA) }
    ramOutWires(i) := rams(i).read(read_elem_counter.cur_valid, read_elem_counter.valid)
  }

  RDATA := MuxLookup(read_elem_counter, ramOutWires(0), for (i <- 0 to n-1) yield i -> ramOutWires(i))

  def getRAMAddrWidth(n: Int) = max((n-1).U.getWidth, 1).W
}
