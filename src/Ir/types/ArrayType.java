package Ir.types;

public class ArrayType implements IRType{
    private final IRType element_type;
    private final int length;

    public ArrayType(IRType element_type) {
        this.element_type = element_type;
        this.length = 0;
    }

    public ArrayType(IRType element_type, int length) {
        this.element_type = element_type;
        this.length = length;
    }

    public IRType getElementType() {
        return element_type;
    }
}
