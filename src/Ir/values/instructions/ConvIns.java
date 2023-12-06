package Ir.values.instructions;

import Ir.types.IntegerType;
import Ir.types.PointerType;
import Ir.types.VoidType;
import Ir.values.BasicBlock;
import Ir.values.Value;

public class ConvIns extends Instruction{
    public ConvIns(BasicBlock block, IROp op, Value value){
        super(VoidType.voidType, op, block);
        this.setName("%d" + reg_num++);
        if (op.equals(IROp.Zext)){
            setType(IntegerType.i32);
        } else if (op.equals(IROp.Bitcast)){
            setType(new PointerType(IntegerType.i32));
        }
        addOperand(value);
    }


    @Override
    public String toString() {
        if (getOp().equals(IROp.Zext)){
            return getName() + " = zext i1 " + getOperands().get(0).getName() + " to i32";
        } else if(getOp().equals(IROp.Bitcast)){
            return getName() + " = bitcast " + getOperands().get(0).getType() + " " + getOperands().get(0).getName() + " to i32*";
        } else {
            return null;
        }
    }
}
