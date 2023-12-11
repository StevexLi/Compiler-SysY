package Ir.values.instructions;

import Ir.types.FunctionType;
import Ir.types.IRType;
import Ir.types.IntegerType;
import Ir.types.VoidType;
import Ir.values.BasicBlock;
import Ir.values.BuildFactory;
import Ir.values.Function;
import Ir.values.Value;

import java.util.ArrayList;

public class CallIns extends Instruction {

    public CallIns(BasicBlock basicBlock, Function func, ArrayList<Value> args) {
        super(((FunctionType)func.getType()).getRet_type(), IROp.Call, basicBlock);
        if (!(((FunctionType) func.getType()).getRet_type() instanceof VoidType)) {
            this.setName("%" + reg_num++);
        }
        this.addOperand(func);

        for (int i=0; i<args.size(); i++){ // Convert Type
            IRType curType = args.get(i).getType();
            IRType realType = ((FunctionType) func.getType()).getParam_type().get(i);
            Value value = convType(args.get(i), basicBlock, curType, realType);
            this.addOperand(value);
        }

        Function cur_func = basicBlock.getNode().getParent().getValue();
        func.addPred(cur_func);
        cur_func.addSucc(func);
    }

    private Value convType(Value value, BasicBlock basicBlock, IRType curType, IRType realType) {
        boolean isCurI1 = curType instanceof IntegerType && ((IntegerType) curType).isIx(1);
        boolean isCurI32 = curType instanceof IntegerType && ((IntegerType) curType).isIx(32);
        boolean isRealI1 = realType instanceof IntegerType && ((IntegerType) realType).isIx(1);
        boolean isRealI32 = realType instanceof IntegerType && ((IntegerType) realType).isIx(32);
        if (!isCurI1 && !isCurI32 && !isRealI1 && !isRealI32) {
            return value;
        } else if ((isCurI1 && isRealI1) || (isCurI32 && isRealI32)) {
            return value;
        } else if (isCurI1 && isRealI32) {
            return BuildFactory.getInstance().buildZext(value, basicBlock);
        } else if (isCurI32 && isRealI1) {
            return BuildFactory.getInstance().buildConvToI1(value, basicBlock);
        } else {
            return value;
        }
    }

    public Function getCalledFunction() {
        return (Function) this.getOperands().get(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IRType ret_type = ((FunctionType)getCalledFunction().getType()).getRet_type();
        if (ret_type instanceof VoidType){
            sb.append("call ");
        } else {
            sb.append(getName()).append(" = call ");
        }
        sb.append(ret_type.toString()).append(" @");
        sb.append(getCalledFunction().getName()).append("(");
        for (int i=1;i<getOperands().size();i++) {
            if (i!=1){
                sb.append(", ");
            }
            sb.append(getOperands().get(i).getType()).append(" ").append(getOperands().get(i).getName());
        }
        sb.append(")");
        return sb.toString();
    }
}
