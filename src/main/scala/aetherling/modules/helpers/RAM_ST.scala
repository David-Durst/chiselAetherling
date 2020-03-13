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
  val RDATA = IO(Output(t.chiselRepr()))
  val WE = IO(Input(Bool()))
  val WADDR = IO(Input(UInt(getRAMAddrWidth(n))))
  val WDATA = IO(Input(t.chiselRepr()))


  val write_elem_counter = Module(new NestedCountersWithNumValid(t, false))
  val read_elem_counter = Module(new NestedCountersWithNumValid(t, false))
  //printf("write_elem_counter_valid: %d\nread_elem_counter_valid: %d\nread_elem_cur_valid: %d\n", write_elem_counter.valid, read_elem_counter.valid, read_elem_counter.cur_valid)
  write_elem_counter.CE := WE
  read_elem_counter.CE := RE

  println(s"Made a memory of size ${t.validClocks()*n} with elements of type ${t.chiselRepr()}")
  val waddr_offsets_rom = VecInit(for (i <- 0 to n-1) yield (i * t.validClocks()).U)
  val raddr_offsets_rom = VecInit(for (i <- 0 to n-1) yield (i * t.validClocks()).U)
  val ram = SyncReadMem(t.validClocks()*n, t.flatChiselRepr())

  when(write_elem_counter.valid) { ram.write(waddr_offsets_rom(WADDR) + write_elem_counter.cur_valid, WDATA.asUInt()) }
  RDATA := ram.read(raddr_offsets_rom(RADDR) + read_elem_counter.cur_valid, read_elem_counter.valid).asTypeOf(RDATA)

  def getRAMAddrWidth(n: Int) = max((n-1).U.getWidth, 1).W
}
