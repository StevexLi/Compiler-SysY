package Ir.types;

public class PointerType implements IRType {
    private IRType point_for;

    public PointerType(IRType for_type) {
        this.point_for = for_type;
    }

    public IRType getTargetType() {
        return point_for;
    }

    @Override
    public String toString() {
        return point_for.toString() + "*";
    }
}
