package Ir.types;

import Ir.values.ConstInt;
import Ir.values.Value;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

    public boolean isString() {
        return element_type instanceof IntegerType && ((IntegerType) element_type).isIx(8);
    }

    public IRType getElementType() {
        return element_type;
    }

    public int getLength() {
        return length;
    }

    /**
     * 获取每层的长度（正序）
     *
     * @return {@link ArrayList}<{@link Integer}>
     */
    public ArrayList<Integer> getDims() {
        ArrayList<Integer> dims = new ArrayList<>();
        for (IRType type = this; type instanceof ArrayType; type = ((ArrayType) type).getElementType()){
            dims.add(((ArrayType) type).getLength());
        }
        return dims;
    }

    /**
     * 获取数组总长
     *
     * @return int
     */
    public int getCapacity(){
        int capacity = 1;
        for (int dim : getDims()){
            capacity *= dim;
        }
        return capacity;
    }

    public ArrayList<Value> offset2Index(int offset){
        ArrayList<Value> index = new ArrayList<>();
        IRType type = this;
        while (type instanceof ArrayType){
            index.add(new ConstInt(offset/((ArrayType)type).getCapacity()));
            offset %= ((ArrayType)type).getCapacity();
            type = ((ArrayType)type).getElementType();
        }
        index.add(new ConstInt(offset));
        return index;
    }



    @Override
    public String toString() {
        return "[" + length + " x " + element_type.toString() + "]";
    }
}
