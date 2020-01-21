package aetherling.modules
import aetherling.modules.helpers._
import aetherling.modules.shift._
import aetherling.modules.higherorder._
import aetherling.types._
import chisel3._


class Module_0() extends MultiIOModule  with UnaryInterface with ValidInterface {
    val I = IO(Input(SSeq(3, SSeq(3, STInt(8))).chiselRepr()))
    val O = IO(Output(SSeq(1, SSeq(1, STInt(8))).chiselRepr()))
    val (const187_O, const187_valid_down) = Const(SSeq(3, SSeq(3, STInt(8))), Const.make_vec(Const.make_vec(0.U(8.W),1.U(8.W),0.U(8.W),1.U(8.W),2.U(8.W),1.U(8.W),0.U(8.W),1.U(8.W),0.U(8.W))), 1, valid_up)
    val n146 = Module(new Map2S(3,new Map2S(3,new AtomTuple(STInt(8), STInt(8)))))
    n146.I0 := I
    n146.I1 := const187_O
    n146.valid_up := valid_up & const187_valid_down
    val n157 = Module(new MapS(3, new MapS(3, new LShift(STInt(8)))))
    n157.I := n146.O
    n157.valid_up := n146.valid_down
    val n162 = Module(new MapS(3, new ReduceS(3, new AddNoValid(STInt(8)), STInt(8))))
    n162.I := n157.O
    n162.valid_up := n157.valid_down
    val n167 = Module(new ReduceS(3, new MapSNoValid(1, new AddNoValid(STInt(8))), SSeq(1, STInt(8))))
    n167.I := n162.O
    n167.valid_up := n162.valid_down
    val (const188_O, const188_valid_down) = Const(SSeq(1, SSeq(1, STInt(8))), Const.make_vec(4.U(8.W)), 3, valid_up)
    val n170 = Module(new Map2S(1,new Map2S(1,new AtomTuple(STInt(8), STInt(8)))))
    n170.I0 := n167.O
    n170.I1 := const188_O
    n170.valid_up := n167.valid_down & const188_valid_down
    val n181 = Module(new MapS(1, new MapS(1, new RShift(STInt(8)))))
    n181.I := n170.O
    n181.valid_up := n170.valid_down
    O := n181.O
    valid_down := n181.valid_down
}

class Top() extends MultiIOModule  with UnaryInterface with ValidInterface {
    val I = IO(Input(TSeq(2, 0, SSeq(8, SSeq(1, SSeq(1, STInt(8))))).chiselRepr()))
    val O = IO(Output(TSeq(2, 0, SSeq(8, SSeq(1, SSeq(1, STInt(8))))).chiselRepr()))
    val n1 = Module(new FIFO(TSeq(2, 0, SSeq(8, SSeq(1, SSeq(1, STInt(8))))), 1))
    n1.I := I
    n1.valid_up := valid_up
    val n2 = Module(new ShiftTS(2, 0, 8, 4, SSeq(1, SSeq(1, STInt(8)))))
    n2.I := n1.O
    n2.valid_up := n1.valid_down
    val n3 = Module(new ShiftTS(2, 0, 8, 4, SSeq(1, SSeq(1, STInt(8)))))
    n3.I := n2.O
    n3.valid_up := n2.valid_down
    val n4 = Module(new ShiftTS(2, 0, 8, 1, SSeq(1, SSeq(1, STInt(8)))))
    n4.I := n3.O
    n4.valid_up := n3.valid_down
    val n5 = Module(new ShiftTS(2, 0, 8, 1, SSeq(1, SSeq(1, STInt(8)))))
    n5.I := n4.O
    n5.valid_up := n4.valid_down
    val n6 = Module(new Map2T(new Map2S(8,new Map2S(1,new Map2S(1,new SSeqTupleCreator(STInt(8)))))))
    n6.I0 := n5.O
    n6.I1 := n4.O
    n6.valid_up := n5.valid_down & n4.valid_down
    val n19 = Module(new Map2T(new Map2S(8,new Map2S(1,new Map2S(1,new SSeqTupleAppender(STInt(8), 2))))))
    n19.I0 := n6.O
    n19.I1 := n3.O
    n19.valid_up := n6.valid_down & n3.valid_down
    val n40 = Module(new MapT(new MapS(8, new MapS(1, new Remove1S(new SSeqTupleToSSeq(STInt(8), 3))))))
    n40.I := n19.O
    n40.valid_up := n19.valid_down
    val n41 = Module(new ShiftTS(2, 0, 8, 1, SSeq(1, SSeq(1, STInt(8)))))
    n41.I := n2.O
    n41.valid_up := n2.valid_down
    val n42 = Module(new ShiftTS(2, 0, 8, 1, SSeq(1, SSeq(1, STInt(8)))))
    n42.I := n41.O
    n42.valid_up := n41.valid_down
    val n43 = Module(new Map2T(new Map2S(8,new Map2S(1,new Map2S(1,new SSeqTupleCreator(STInt(8)))))))
    n43.I0 := n42.O
    n43.I1 := n41.O
    n43.valid_up := n42.valid_down & n41.valid_down
    val n56 = Module(new Map2T(new Map2S(8,new Map2S(1,new Map2S(1,new SSeqTupleAppender(STInt(8), 2))))))
    n56.I0 := n43.O
    n56.I1 := n2.O
    n56.valid_up := n43.valid_down & n2.valid_down
    val n77 = Module(new MapT(new MapS(8, new MapS(1, new Remove1S(new SSeqTupleToSSeq(STInt(8), 3))))))
    n77.I := n56.O
    n77.valid_up := n56.valid_down
    val n78 = Module(new Map2T(new Map2S(8,new Map2S(1,new SSeqTupleCreator(SSeq(3, STInt(8)))))))
    n78.I0 := n40.O
    n78.I1 := n77.O
    n78.valid_up := n40.valid_down & n77.valid_down
    val n88 = Module(new ShiftTS(2, 0, 8, 1, SSeq(1, SSeq(1, STInt(8)))))
    n88.I := n1.O
    n88.valid_up := n1.valid_down
    val n89 = Module(new ShiftTS(2, 0, 8, 1, SSeq(1, SSeq(1, STInt(8)))))
    n89.I := n88.O
    n89.valid_up := n88.valid_down
    val n90 = Module(new Map2T(new Map2S(8,new Map2S(1,new Map2S(1,new SSeqTupleCreator(STInt(8)))))))
    n90.I0 := n89.O
    n90.I1 := n88.O
    n90.valid_up := n89.valid_down & n88.valid_down
    val n103 = Module(new Map2T(new Map2S(8,new Map2S(1,new Map2S(1,new SSeqTupleAppender(STInt(8), 2))))))
    n103.I0 := n90.O
    n103.I1 := n1.O
    n103.valid_up := n90.valid_down & n1.valid_down
    val n124 = Module(new MapT(new MapS(8, new MapS(1, new Remove1S(new SSeqTupleToSSeq(STInt(8), 3))))))
    n124.I := n103.O
    n124.valid_up := n103.valid_down
    val n125 = Module(new Map2T(new Map2S(8,new Map2S(1,new SSeqTupleAppender(SSeq(3, STInt(8)), 2)))))
    n125.I0 := n78.O
    n125.I1 := n124.O
    n125.valid_up := n78.valid_down & n124.valid_down
    val n141 = Module(new MapT(new MapS(8, new Remove1S(new SSeqTupleToSSeq(SSeq(3, STInt(8)), 3)))))
    n141.I := n125.O
    n141.valid_up := n125.valid_down
    val n183 = Module(new MapT(new MapS(8, new Module_0())))
    n183.I := n141.O
    n183.valid_up := n141.valid_down
    val n184 = Module(new FIFO(TSeq(2, 0, SSeq(8, SSeq(1, SSeq(1, STInt(8))))), 1))
    n184.I := n183.O
    n184.valid_up := n183.valid_down
    val n185 = Module(new FIFO(TSeq(2, 0, SSeq(8, SSeq(1, SSeq(1, STInt(8))))), 1))
    n185.I := n184.O
    n185.valid_up := n184.valid_down
    val n186 = Module(new FIFO(TSeq(2, 0, SSeq(8, SSeq(1, SSeq(1, STInt(8))))), 1))
    n186.I := n185.O
    n186.valid_up := n185.valid_down
    O := n186.O
    valid_down := n186.valid_down
}



import aetherling.modules._
import chisel3.iotesters.ChiselFlatSpec

class TopTest(c: Top) extends NestedPeekPokeTester(c) {
    val chisel_inputs0 = Array(Array(1,2,3,4,5,6,7,8),Array(9,10,11,12,13,14,15,16))
    val chisel_inputs0_valid = Array(true,true)
    val chisel_output = Array(Array(253,253,253,253,253,253,253,253),Array(253,253,6,7,253,253,10,11))
    val chisel_output_valid = Array(true,true)
    poke_nested(c.valid_up, 1.B)
    var output_counter = 0
    val run_clks = 2
    val pipeline_clks = 6
    val total_clks = run_clks + pipeline_clks
    for(f_clk <- 0 to (total_clks - 1)) {
        println(s"clk: $f_clk")
        if (f_clk < run_clks  && chisel_inputs0_valid(f_clk)) {
            poke_nested(c.I, nest_indexed_seq(chisel_inputs0(f_clk),compute_num_atoms_per_sseq_layer(c.I)))
        }
        peek_unary_module(c)
        if(f_clk > 6){
            output_counter += 1
        }
        if(f_clk >= 6){
            expect_nested(c.valid_down, 1)
        }
        if(f_clk >= pipeline_clks &&  chisel_output_valid(output_counter)){
            expect_nested(c.O, nest_indexed_seq(chisel_output(output_counter),compute_num_atoms_per_sseq_layer(c.O)))
        }
        step(1)
    }
}

class TopTester extends ChiselFlatSpec {
    "Top" should "behave correctly" in {
        iotesters.Driver.execute(Array("--backend-name", "verilator","--target-dir","test_run_dir/top"), () => new Top()) {
            c => new TopTest(c)
        } should be(true)
    }
}