package aetherling.modules.helpers

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

class RAM_ST(t: STTypeDefinition, n: Int) extends MultiIOModule {
  val RE = IO(Input(Bool()))
  val RADDR = IO(Input(t.chiselRepr()))
  val RDATA = IO(Output(t.chiselRepr()))
  val WE = IO(Input(Bool()))
  val WADDR = IO(Input(t.chiselRepr()))
  val WDATA = IO(Input(t.chiselRepr()))


  val rams = for (_ <- (0 to n-1)) yield SyncReadMem(t.validClocks(), t.chiselRepr())

  val (read_counter_value, _) = Counter(RE, t.time())
  val (write_counter_value, _) = Counter(WE, t.time())


}
