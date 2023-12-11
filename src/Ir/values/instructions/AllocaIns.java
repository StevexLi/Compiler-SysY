package Ir.values.instructions;

import Ir.types.ArrayType;
import Ir.types.IRType;
import Ir.types.PointerType;
import Ir.values.BasicBlock;

public class AllocaIns extends MemIns {
    private boolean is_const;
    private IRType alloca_type;


    public AllocaIns(BasicBlock block, boolean is_const, IRType type) {
        super(new PointerType(type), IROp.Alloca, block);
        this.is_const = is_const;
        this.alloca_type = type;
        this.setName("%" + reg_num++);
        if (alloca_type instanceof ArrayType) { // 数组
            if (((ArrayType) alloca_type).getLength() == -1) {
                this.alloca_type = new PointerType(((ArrayType) alloca_type).getElementType());
                setType(new PointerType(this.alloca_type));
            }
        }
    }


    public IRType getAlloca_type() {
        return alloca_type;
    }


    @Override
    public String toString() {
        return this.getName() + " = alloca " + this.getAlloca_type().toString();
    }
}
