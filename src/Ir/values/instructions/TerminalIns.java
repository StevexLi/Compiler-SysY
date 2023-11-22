package Ir.values.instructions;

import Ir.types.IRType;
import Ir.values.BasicBlock;

public abstract class TerminalIns extends Instruction {

    public TerminalIns(IRType type, IROp op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
