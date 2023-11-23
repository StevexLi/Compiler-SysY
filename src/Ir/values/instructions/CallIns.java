package Ir.values.instructions;

import Ir.types.FunctionType;
import Ir.types.IRType;
import Ir.types.VoidType;
import Ir.values.BasicBlock;
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

        for (int i=0; i<args.size(); i++){ // TODO:Convert Type
            Value value = args.get(i);
            this.addOperand(value);
        }

        Function cur_func = basicBlock.getNode().getParent().getValue();
        func.addPred(cur_func);
        cur_func.addSucc(func);
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
