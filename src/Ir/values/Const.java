package Ir.values;

import Ir.types.IRType;

public abstract class Const extends Value {

    public Const(String name, IRType type) {
        super(name, type);
    }
}
