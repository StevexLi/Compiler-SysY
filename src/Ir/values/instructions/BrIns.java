package Ir.values.instructions;

import Ir.types.IRType;
import Ir.values.BasicBlock;

public class BrIns extends TerminalIns { // TODO: BranchIns

    public BrIns(IRType type, IROp op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
