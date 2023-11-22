package Ir.types;

public class IntegerType implements IRType {
    /**
     * 位宽
     */
    private int bit;

    private IntegerType(int bit){
        this.bit = bit;
    }

    public static final IntegerType i1 = new IntegerType(1);
    public static final IntegerType i8 = new IntegerType(8);
    public static final IntegerType i32 = new IntegerType(32);
    public boolean isIx(int x){
        return this.bit == x;
    }

    @Override
    public String toString() {
        return "i" + bit;
    }
}
