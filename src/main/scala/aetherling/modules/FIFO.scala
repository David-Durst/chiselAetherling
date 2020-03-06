package aetherling.modules

import aetherling.modules.helpers.{UnaryInterface, ValidInterface}
import aetherling.types.{STAtomTuple, STTypeDefinition}
import chisel3._
import chisel3.util.Counter

class FIFO(t: STTypeDefinition, delay: Int) extends MultiIOModule with UnaryInterface with ValidInterface {
  override val I = IO(Input(t.chiselRepr()))
  override val O = IO(Output(t.chiselRepr()))

  if (delay == 1) {
    val dataReg = RegNext(I)
    O := dataReg
    val validReg = RegNext(valid_up, false.B)
    valid_down := validReg
  }
  else {
    val internalDelayCounter = Counter(delay + 1)
    val readCounter = Counter(delay + 1)
    val writeCounter = Counter(delay + 1)
    println(s"Made memory of size ${delay + 1} with elements of type ${t.chiselRepr()}")
    val fifoBuffer = SyncReadMem(delay + 1, t.chiselRepr())

    /*
    printf("idc cur value: %d\n", internalDelayCounter.value)
    printf("delay: %d\n", delay.U)
    printf("write cur value: %d\n", writeCounter.value)
    printf("read cur value: %d\n", readCounter.value)
    printf("valid cur clock: %d\n", internalDelayCounter.value < (delay - 1).U)
    printf("op valid cur clock: %d\n", internalDelayCounter.value >= (delay - 1).U)
     */
    valid_down := internalDelayCounter.value === delay.U
    when(valid_up) {
      writeCounter.inc()
      fifoBuffer.write(writeCounter.value, I)

      when(internalDelayCounter.value < delay.U) {
        printf("idc inc\n")
        internalDelayCounter.inc()
      }
      when(internalDelayCounter.value >= (delay - 1).U) {
        O := fifoBuffer.read(readCounter.value, internalDelayCounter.value >= (delay - 1).U)
        readCounter.inc()
      }.otherwise { O := DontCare }
    }.otherwise {
      O := DontCare
    }
  }
}
