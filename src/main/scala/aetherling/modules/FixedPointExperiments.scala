package aetherling.modules
import aetherling.modules.helpers.{NullaryInterface, ValidInterface}
import aetherling.types.STTypeDefinition
import chisel3.{Input, MultiIOModule, Output}
import chisel3.experimental.FixedPoint
import chisel3._
import chisel3.util.Cat

class FixedPointExperiments() extends MultiIOModule with NullaryInterface with ValidInterface {
    val fp_type = FixedPoint(36.W, 4.BP)
    override val O = IO(Output(fp_type))
    val O_UInt = IO(Output(UInt(38.W)))
    O := FixedPoint.fromDouble(0.75,36.W,4.BP)
    O_UInt := Cat(0.U, FixedPoint.fromDouble(0.0625,36.W,4.BP).asUInt())
    valid_down := valid_up
}
