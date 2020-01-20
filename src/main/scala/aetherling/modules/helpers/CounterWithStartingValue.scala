package aetherling.modules.helpers

import chisel3._
import chisel3.util.{isPow2, log2Ceil}

class CounterWithStartingValue(val n: Int, val start: Int) extends MultiIOModule {
  require(n >= 0)
  val value = if (n > 1) RegInit(start.U(log2Ceil(n+1).W)) else 0.U

  if (n > 1) {
    if (isPow2(n)) {
      value := value + 1.U
    }
    else {
      when (value === (n-1).U) { value := 0.U } otherwise { value := value + 1.U}
    }
  }
}

