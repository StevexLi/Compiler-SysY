package Ir.types;

import java.util.ArrayList;

public class FunctionType implements IRType {
    private ArrayList<IRType> param_type;
    private IRType ret_type;

    public FunctionType(IRType ret_type, ArrayList<IRType> param_type) {
        this.ret_type = ret_type;
        this.param_type = param_type;
        // TODO: array_param
    }

    public ArrayList<IRType> getParam_type() {
        return param_type;
    }
    public IRType getRet_type() {
        return ret_type;
    }
}
