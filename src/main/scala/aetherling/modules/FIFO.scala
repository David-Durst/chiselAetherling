package aetherling.modules

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import aetherling.types.{STAtomTuple, STTypeDefinition}
import chisel3._
import chisel3.util.Counter

class FIFO(t: STTypeDefinition, delay: Int) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val in = IO(Input(t.chiselRepr()))
  override val out = IO(Output(t.chiselRepr()))

  if (delay == 1) {
    val dataReg = RegNext(in)
    out := dataReg
    val validReg = RegNext(valid_up)
    valid_down := validReg
  }
  else {
    val internalDelayCounter = Counter(delay + 1)
    val readCounter = Counter(delay)
    val writeCounter = Counter(delay)
    val fifoBuffer = SyncReadMem(delay, t.chiselRepr())

    printf("idc cur value: %d", internalDelayCounter.value)
    when(valid_up) {
      writeCounter.inc()
      when(internalDelayCounter.value < delay.U) {
        printf("idc inc")
        internalDelayCounter.inc()
      }
      when(internalDelayCounter.value === delay.U) {
        readCounter.inc()
        valid_down := true.B
        out := fifoBuffer.read(readCounter.value, internalDelayCounter.value === delay.U)
      }.otherwise {
        out := DontCare
        valid_down := false.B
      }
      fifoBuffer.write(writeCounter.value, in)
    }.otherwise {
      valid_down := false.B
      out := DontCare
    }
  }
}
