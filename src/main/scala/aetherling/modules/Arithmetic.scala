package aetherling.modules

import aetherling.modules.helpers._
import aetherling.types._
import chisel3._

/**
  * Add two Int atoms
  * @param t the Space-Time Int type (specifies width)
  */
class Add(t: ST_Int) extends BinaryModule(t.chiselRepr(), t.chiselRepr(), t.chiselRepr()) {
  io.out := io.in0 + io.in1
  io.valids.valid_down := io.valids.valid_up
}

/**
  * Abs of an Int atom
  * @param t the Space-Time Int type (specifies width)
  */
class Abs(t: ST_Int) extends UnaryModule(t.chiselRepr(), t.chiselRepr())  {
  when(io.in.asSInt() < 0.S) { io.out := 0.U - io.in }.otherwise( io.out := io.in )
  io.valids.valid_down := io.valids.valid_up
}
