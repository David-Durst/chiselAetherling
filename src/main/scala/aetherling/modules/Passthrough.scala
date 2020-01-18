package aetherling.modules

import aetherling.modules.helpers._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._
import chisel3.util.Counter

class Passthrough(t_in: STTypeDefinition, t_out: STTypeDefinition) extends MultiIOModule
  with UnaryInterface with ValidInterface {
  val I = IO(Input(t_in.chiselRepr()))
  val O = IO(Output(t_out.chiselRepr()))
  val I_flattened = ChiselValueOperators.flattenChiselValue(I)
  val O_flattened = ChiselValueOperators.flattenChiselValue(O)
  require(I_flattened.size == O_flattened.size, "I and O not same length when flattened for passthrough.")

  I_flattened zip O_flattened map { case(i_elem, o_elem) => o_elem := i_elem }
  valid_down := valid_up
}
