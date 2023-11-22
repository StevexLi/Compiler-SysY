package Ir.values.instructions;

import Ir.types.IRType;
import Ir.values.BasicBlock;

public class StoreIns extends Instruction {

    public StoreIns(IRType type, IROp op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
