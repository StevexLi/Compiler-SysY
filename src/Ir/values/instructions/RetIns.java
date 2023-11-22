package Ir.values.instructions;

import Ir.types.VoidType;
import Ir.values.BasicBlock;
import Ir.values.Value;

public class RetIns extends Instruction {
    public RetIns(BasicBlock block) {
        super(VoidType.voidType, IROp.Ret, block);
    }

    public RetIns(BasicBlock block, Value ret) {
        super(ret.getType(), IROp.Ret, block);
        this.addOperand(ret);
    }


    public boolean isVoid() {
        return this.getOperands().isEmpty();
    }

    @Override
    public String toString() {
        if (getOperands().size()==1){
            return "ret " + getOperands().get(0).getType() + " " + getOperands().get(0).getName();
        } else {
            return "ret void";
        }
    }
}
