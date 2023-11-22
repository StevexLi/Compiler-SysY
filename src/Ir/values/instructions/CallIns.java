package Ir.values.instructions;

import Ir.types.IRType;
import Ir.values.BasicBlock;

public class CallIns extends Instruction {

    public CallIns(IRType type, IROp op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
