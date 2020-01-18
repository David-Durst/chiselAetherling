package aetherling.modules.helpers

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

object ChiselValueOperators {

  /**
    * Given a chisel value containing aggregates, return a Vector with all the non-aggregate Data values
    */
  def flattenChiselValue(valueToFlatten: Data): IndexedSeq[Data] = {
    valueToFlatten

    val flat_in_ports = get_nested_ports(cls.I, num_nested_space_layers(t_in), [])
    flat_out_ports = get_nested_ports(cls.O, num_nested_space_layers(t_out), [])
    for i_port, o_port in zip(flat_in_ports, flat_out_ports):
      wire(i_port, o_port)
    if has_valid:
      wire(cls.valid_up, cls.valid_down)
  }

}
