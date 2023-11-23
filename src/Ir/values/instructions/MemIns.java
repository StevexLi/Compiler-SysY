package Ir.values.instructions;

import Ir.types.IRType;
import Ir.values.BasicBlock;

public abstract class MemIns extends Instruction {


    public MemIns(IRType type, IROp op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
