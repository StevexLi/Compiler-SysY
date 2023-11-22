package Ir.values;

import Ir.IRModule;
import Ir.types.IRType;
import Ir.types.PointerType;

public class GlobalVar extends User{
    private boolean is_const;
    private Value value;

    public GlobalVar(String name, IRType type, boolean is_const, Value value) {
        super("@" + name, new PointerType(type));
        this.is_const = is_const;
        this.value = value;
        IRModule.getInstance().addGlobalVar(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = ");
        if (is_const)
            sb.append("constant ");
        else
            sb.append("global ");
        if (value!=null)
            sb.append(value);
        return sb.toString();
    }
}
