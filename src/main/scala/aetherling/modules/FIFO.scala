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
    val internalDelayCounter = Counter(delay)
    val readCounter = Counter(delay + 1)
    val writeCounter = Counter(delay + 1)
    val fifoBuffer = SyncReadMem(delay + 1, t.chiselRepr())

    printf("idc cur value: %d\n", internalDelayCounter.value)
    printf("write cur value: %d\n", writeCounter.value)
    printf("read cur value: %d\n", readCounter.value)
    when(valid_up) {
      writeCounter.inc()
      fifoBuffer.write(writeCounter.value, in)

      when(internalDelayCounter.value < (delay - 1).U) {
        printf("idc inc\n")
        internalDelayCounter.inc()
        out := DontCare
        valid_down := false.B
      }.otherwise {
        readCounter.inc()
        valid_down := true.B
        out := fifoBuffer.read(readCounter.value, internalDelayCounter.value === (delay - 1).U)
      }
    }.otherwise {
      valid_down := false.B
      out := DontCare
    }
  }
}
