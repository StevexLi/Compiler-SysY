package Ir.values.instructions;

import Ir.types.IRType;
import Ir.types.IntegerType;
import Ir.types.VoidType;
import Ir.values.BasicBlock;
import Ir.values.BuildFactory;
import Ir.values.ConstInt;
import Ir.values.Value;


public class BrIns extends TerminalIns { // TODO: BranchIns
    public BrIns(BasicBlock block, BasicBlock true_block) {
        super(VoidType.voidType, IROp.Br, block);
        this.addOperand(true_block);
        if (block!=null) {
            if ((block.getIns().getEnd() == null) || (!(block.getIns().getEnd().getValue() instanceof BrIns) && !(block.getIns().getEnd().getValue() instanceof RetIns))){
                block.addSuccessor(true_block);
                true_block.addPredecessor(block);
            }
        }
    }

    public BrIns(BasicBlock block, Value cond, BasicBlock true_block, BasicBlock false_block) {
        super(VoidType.voidType, IROp.Br, block);
        Value tmp_cond = cond;
        if (!(cond.getType() instanceof IntegerType && ((IntegerType) cond.getType()).isIx(1))){
           tmp_cond = BuildFactory.getInstance().buildBinary(block, IROp.Ne, cond, new ConstInt(0));
        }
        this.addOperand(tmp_cond);
        this.addOperand(true_block);
        this.addOperand(false_block);
        if (block!=null) {
            if ((block.getIns().getEnd() == null) || (!(block.getIns().getEnd().getValue() instanceof BrIns) && !(block.getIns().getEnd().getValue() instanceof RetIns))){
                block.addSuccessor(true_block);
                block.addSuccessor(false_block);
                true_block.addPredecessor(block);
                false_block.addPredecessor(block);
            }
        }
    }

    @Override
    public String toString() {
        if (this.getOperands().size()==1){ // 无条件跳转
            return "br label %" + this.getOperands().get(0).getName();
        } else { // 有条件跳转
            return "br " + this.getOperands().get(0).getType() + " " + this.getOperands().get(0).getName() + ", label %" + this.getOperands().get(1).getName() + ", label %" + this.getOperands().get(2).getName();
        }
    }
}
