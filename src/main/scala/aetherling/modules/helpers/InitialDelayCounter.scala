package aetherling.modules.helpers

import chisel3.util.log2Ceil
import chisel3._

class InitialDelayCounter(val n: Int) extends MultiIOModule with ValidInterface {
  require(n >= 0)
  val value = if (n >= 1) RegInit(0.U(log2Ceil(n+1).W)) else 0.U


  /** Increment the counter, returning whether the counter currently is at the
    * maximum and will wrap. The incremented value is registered and will be
    * visible on the next cycle.
    */
  if (n >= 1) {
    valid_down := value === n.U
    when (value < n.U && valid_up) { value := value + 1.U }
  } else {
    valid_down := valid_up
  }
}

