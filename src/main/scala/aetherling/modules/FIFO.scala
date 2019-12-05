package aetherling.modules

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import aetherling.types.{STAtomTuple, STTypeDefinition}
import chisel3._
import chisel3.util.Counter

class FIFO(t: STTypeDefinition, delay: Int) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val in = IO(Input(t.chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))

  if (delay == 1) {
    val dataReg = Reg(in)
    out := dataReg
    val validReg = Reg(valid_up)
    valid_down := validReg
  }
  else {
    val internalDelayCounter = Module(Counter(delay + 1))
    val readCounter = Module(Counter(delay))
    val writeCounter = Module(Counter(delay))
    val fifoBuffer = SyncReadMem(delay, t.chiselRepr())

    when(valid_up) {
      writeCounter.inc()
      when(internalDelayCounter.value < delay.U) {
        internalDelayCounter.inc()
      }
      when(internalDelayCounter.value === delay.U) {
        readCounter.inc()
        valid_down := true.B
      }.otherwise( valid_down := false.B )
      fifoBuffer.write(writeCounter.value, in)
      out := fifoBuffer.read(readCounter.value, valid_up)
    }
  }
}
