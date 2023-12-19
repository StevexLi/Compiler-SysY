package Ir.values.instructions;

import Ir.types.ArrayType;
import Ir.types.IRType;
import Ir.values.Const;
import Ir.values.ConstInt;
import Ir.values.Value;

import java.util.ArrayList;
import java.util.List;

public class ConstArray extends Const{
    private IRType elem_type;
    private ArrayList<Value> arr;
    private int capacity;
    private boolean init = false;

    public ConstArray(IRType type, IRType elem_type, int capacity) {
        super("name", type);
        this.elem_type = elem_type;
        this.capacity = capacity;
        this.arr = new ArrayList<>();
        if (this.elem_type instanceof ArrayType) {
            for (int i = 0; i < ((ArrayType) type).getLength(); i++) {
                arr.add(new ConstArray(this.elem_type, ((ArrayType) this.elem_type).getElementType(), ((ArrayType) this.elem_type).getCapacity()));
            }
        } else {
            for (int i = 0; i < ((ArrayType) type).getLength(); i++) {
                arr.add(ConstInt.ZERO);
            }
        }
    }

    public ArrayList<Value> get1DArray() {
        ArrayList<Value> result = new ArrayList<>();
        for (Value value : arr) {
            if (value instanceof ConstArray) {
                result.addAll(((ConstArray) value).get1DArray());
            } else {
                result.add(value);
            }
        }
        return result;
    }

    public boolean allZero() {
        for (Value value : arr) {
            if (value instanceof ConstInt) {
                if (((ConstInt) value).getValue() != 0) {
                    return false;
                }
            } else {
                if (!((ConstArray) value).allZero()) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean isInit() {
        return init || !allZero();
    }

    public void setInit(boolean init){
        this.init = init;
    }

    public void storeValue(int offset, Value value){
        if (elem_type instanceof ArrayType){
            ((ConstArray) (arr.get(offset / ((ArrayType) elem_type).getCapacity()))).storeValue(offset % ((ArrayType) elem_type).getCapacity(), value);
        } else {
            arr.set(offset, value);
        }
    }

    /**
     * 判断是否全部为零（递归）
     *
     * @return boolean
     */
    public boolean isAllZero(){
        for (Value value : arr) {
            if (value instanceof ConstInt) {
                if (((ConstInt) value).getValue() != 0) {
                    return false;
                }
            } else {
                if (!((ConstArray) value).isAllZero()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (isAllZero()){
            return this.getType().toString() + " zeroinitializer";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getType().toString()).append(" [");
            for (int i=0; i<arr.size(); i++){
                if (i!=0){
                    sb.append(", ");
                }
                sb.append(arr.get(i).toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
