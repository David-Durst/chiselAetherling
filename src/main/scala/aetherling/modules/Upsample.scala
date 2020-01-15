package aetherling.modules
import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

class UpS(n: Int, elem_t: STTypeDefinition) extends MultiIOModule  with UnaryInterface with ValidInterface {
  val I = IO(Input(SSeq(1, elem_t).chiselRepr()))
  val O = IO(Output(SSeq(n, elem_t).chiselRepr()))

  for (i <- 0 to (n-1)) {
    O(i) := I(0)
  }
  valid_down := valid_up
}

class UpT(n: Int, i: Int, elem_t: STTypeDefinition) extends MultiIOModule  with UnaryInterface with ValidInterface {
  val I = IO(Input(TSeq(1, n + i - 1, elem_t).chiselRepr()))
  val O = IO(Output(TSeq(n, i, elem_t).chiselRepr()))
  O := I

  val (element_time_counter_value, _) = Counter(valid_up, elem_t.time())
  val (element_idx_counter_value, _) =
    Counter(valid_up && (element_time_counter_value === (elem_t.time() - 1).U), n + i)

  // Create a synchronous-read, synchronous-write memory (like in FPGAs SRAMs).
  // using memory rather than registers as can be larger and want synthesizer to pick mem or reg
  val mem = SyncReadMem(1, elem_t.chiselRepr())
  // Create one port memory for read and write
  val dataOut = Wire(elem_t.chiselRepr())
  when(element_idx_counter_value === 0.U) { mem.write(0.U, I); dataOut := DontCare }
    .otherwise( dataOut := mem.read(0.U, valid_up) )

  when(element_idx_counter_value === 0.U) { O := I } otherwise { O := dataOut }

  valid_down := valid_up
}
