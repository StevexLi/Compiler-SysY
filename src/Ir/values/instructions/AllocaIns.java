package Ir.values.instructions;

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
        // TODO:Array
    }


    public IRType getAlloca_type() {
        return alloca_type;
    }


    @Override
    public String toString() {
        return this.getName() + " = alloca " + this.getAlloca_type().toString();
    }
}
