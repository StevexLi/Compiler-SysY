package Ir.values.instructions;

import Ir.types.ArrayType;
import Ir.types.IRType;
import Ir.types.PointerType;
import Ir.values.BasicBlock;
import Ir.values.GlobalVar;
import Ir.values.Value;

import java.util.ArrayList;

public class GEPIns extends MemIns {
    private IRType elem_type;
    private Value target_item;

    public GEPIns(BasicBlock block, Value pointer, int offset){
        this(block, pointer, ((ArrayType) ((PointerType) pointer.getType()).getTargetType()).offset2Index(offset));
    }

    public GEPIns(BasicBlock block, Value pointer, ArrayList<Value> indice){
        super(new PointerType(getElementType(pointer, indice)), IROp.GEP, block);
        this.setName("%"+reg_num++);
        if (pointer instanceof GEPIns){
            target_item = ((GEPIns)pointer).target_item;
        } else if (pointer instanceof AllocaIns){
            target_item = pointer;
        } else if (pointer instanceof GlobalVar){
            target_item = pointer;
        }
        this.addOperand(pointer);
        for (Value value : indice){
            this.addOperand(value);
        }
        this.elem_type = getElementType(pointer, indice);
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    private static IRType getElementType(Value pointer, ArrayList<Value> indice){
        IRType type = pointer.getType();
        for (Value value : indice){
            if (type instanceof ArrayType){
                type = ((ArrayType) type).getElementType();
            } else if (type instanceof PointerType) {
                type = ((PointerType) type).getTargetType();
            } else {
                break;
            }
        }
        return type;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getName()).append(" = getelementptr ");
        if (getPointer().getType() instanceof PointerType && ((PointerType) getPointer().getType()).isString()){ // 字符串
            s.append("inbounds ");
        }
        s.append(((PointerType)getPointer().getType()).getTargetType()).append(", ");
        for (int i=0;i<getOperands().size();i++){
            if (i==0){
                s.append(getPointer().getType()).append(" ").append(getPointer().getName());
            } else {
                s.append(", ").append(getOperands().get(i).getType()).append(" ").append(getOperands().get(i).getName());
            }
        }
        return s.toString();
    }
}
