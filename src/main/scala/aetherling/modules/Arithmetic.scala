package aetherling.modules

import chisel3._

/**
  * Add two Int atoms
  * @param width bit width of Ints
  */
class Add(width: Int) extends Module {
  val io = IO(new Bundle {
    val in0        = Input(UInt(width.W))
    val in1        = Input(UInt(width.W))
    val out        = Output(UInt(width.W))
    val valids     = new ValidPorts
  })
  io.out := io.in0 + io.in1
  io.valids.valid_down := io.valids.valid_up
}

/**
  * Abs of an Int atom
  * @param width bit width of Int
  */
class Abs(width: Int) extends Module {
  val io = IO(new Bundle {
    val in        = Input(UInt(width.W))
    val out        = Output(UInt(width.W))
    val valids     = new ValidPorts
  })
  when(io.in.asSInt() < 0.S) { io.out := 0.U - io.in }.otherwise( io.out := io.in )
  io.valids.valid_down := io.valids.valid_up
}
