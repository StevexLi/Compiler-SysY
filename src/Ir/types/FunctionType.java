package Ir.types;

import java.util.ArrayList;
import java.util.List;

public class FunctionType implements IRType {
    private ArrayList<IRType> param_type;
    private IRType ret_type;

    public FunctionType(IRType ret_type, ArrayList<IRType> param_type) {
        this.ret_type = ret_type;
        this.param_type = param_type;
        arrayTypeNoLength();
    }

    private void arrayTypeNoLength() {
        List<Integer> target = new ArrayList<>();
        for (IRType type : param_type) {
            if (type instanceof ArrayType) {
                if (((ArrayType) type).getLength() == -1) {
                    target.add(param_type.indexOf(type));
                }
            }
        }
        for (int index : target) {
            param_type.set(index, new PointerType(((ArrayType) param_type.get(index)).getElementType()));
        }
    }

    public ArrayList<IRType> getParam_type() {
        return param_type;
    }
    public IRType getRet_type() {
        return ret_type;
    }
}
