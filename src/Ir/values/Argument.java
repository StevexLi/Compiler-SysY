package Ir.values;

import Ir.types.IRType;

public class Argument extends Value {
    private int index;

    public Argument(IRType type, int index, boolean isLibFunc) {
        super(isLibFunc ? "" : "%" + reg_num++, type);
        this.index = index;
    }

    @Override
    public String toString() {
        return this.getType().toString() + " " + this.getName();
    }
}
