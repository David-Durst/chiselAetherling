package aetherling.modules
import aetherling.modules.helpers.{NullaryInterface, ValidInterface}
import aetherling.types.STTypeDefinition
import chisel3.{Input, MultiIOModule, Output}
import chisel3.experimental.FixedPoint
import chisel3._

class FixedPointExperiments() extends MultiIOModule with NullaryInterface with ValidInterface {
    val fp_type = FixedPoint(36.W, 4.BP)
    override val O = IO(Output(fp_type))
    O := FixedPoint.fromDouble(0.75,36.W,4.BP)
    valid_down := valid_up
}
