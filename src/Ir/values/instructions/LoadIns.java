package Ir.values.instructions;

import Ir.types.ArrayType;
import Ir.types.PointerType;
import Ir.values.BasicBlock;
import Ir.values.Value;

public class LoadIns extends MemIns {
    public LoadIns(BasicBlock block, Value pointer) {
        super(((PointerType) pointer.getType()).getTargetType(), IROp.Load, block);
        this.setName("%" + reg_num++);
        if (getType() instanceof ArrayType) {
            setType(new PointerType(((ArrayType) getType()).getElementType()));
        }
        this.addOperand(pointer);
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    public Value getIndex() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return getName() + " = load " + getType() + ", " + getPointer().getType() + " " + getPointer().getName();
    }
}
