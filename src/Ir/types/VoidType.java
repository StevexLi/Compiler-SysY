package Ir.types;

public class VoidType implements IRType{
    public static final VoidType voidType = new VoidType();

    @Override
    public String toString() {
        return "void";
    }
}
