package Ir.values.instructions;

import Ir.types.IRType;
import Ir.types.IntegerType;
import Ir.types.VoidType;
import Ir.values.BasicBlock;
import Ir.values.Value;

public class BinaryIns extends Instruction {

    public BinaryIns(BasicBlock basicBlock, IROp op, Value left, Value right) {
        super(VoidType.voidType, op, basicBlock);
        boolean isLeftI1 = left.getType() instanceof IntegerType && ((IntegerType) left.getType()).isIx(1);
        boolean isRightI1 = right.getType() instanceof IntegerType && ((IntegerType) right.getType()).isIx(1);
        boolean isLeftI32 = left.getType() instanceof IntegerType && ((IntegerType) left.getType()).isIx(32);
        boolean isRightI32 = right.getType() instanceof IntegerType && ((IntegerType) right.getType()).isIx(32);
        if (isLeftI1 && isRightI32) {
//            addOperands(BuildFactory.getInstance().buildZext(left, basicBlock), right);
        } else if (isLeftI32 && isRightI1) {
//            addOperands(left, BuildFactory.getInstance().buildZext(right, basicBlock));
        } else {
            addOperands(left, right);
        }
        this.setType(this.getOperands().get(0).getType());
//        if (isCond()) {
//            this.setType(IntegerType.i1);
//        } TODO:是条件表达式
        this.setName("%" + reg_num++);
    }

    private void addOperands(Value left, Value right) {
        this.addOperand(left);
        this.addOperand(right);
    }

    @Override
    public String toString() {
        String s = getName() + " = ";
        switch (this.getOp()){
            case Add:
                s += "add i32 ";
                break;
            case Sub:
                s += "sub i32 ";
                break;
            case Mul:
                s += "mul i32 ";
                break;
            case Div:
                s += "sdiv i32 ";
                break;
            case Mod:
                s += "srem i32 ";
                break;
            default: // TODO: Other ops
                break;
        }
        return s + this.getOperands().get(0).getName() + ", " + this.getOperands().get(1).getName();
    }
}
