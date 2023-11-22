package Ir.values;

import Ir.types.IRType;

import java.util.ArrayList;

public abstract class User extends Value {
    private ArrayList<Value> operands;

    public User(String name, IRType type) {
        super(name, type);
        this.operands = new ArrayList<>();
    }

    public void addOperand(Value operand) {
        this.operands.add(operand);
        if (operand!=null){
            operand.addUse(new Use(operand, this, operands.size()-1));
        }
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }
    public void removeUseFromOperands() {
        if (operands==null)
            return;
        for (Value operand : operands) {
            if (operand!=null)
                operand.removeUseByUser(this);
        }
    }
}
