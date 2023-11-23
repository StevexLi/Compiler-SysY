package Ir.values.instructions;

import Ir.types.IRType;
import Ir.values.BasicBlock;
import Ir.values.Value;

public class StoreIns extends Instruction {

    public StoreIns(BasicBlock block, Value pointer, Value value) {
        super(value.getType(), IROp.Store, block);
        this.addOperand(value);
        this.addOperand(pointer);
    }

    public Value getValue() {
        return getOperands().get(0);
    }

    public Value getPointer() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return "store " + getValue().getType().toString() + " " + getValue().getName() + ", " + getPointer().getType().toString() + " " + getPointer().getName();
    }
}
