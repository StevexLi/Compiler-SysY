package Ir.values.instructions;

import Ir.types.IRType;
import Ir.types.IntegerType;
import Ir.types.VoidType;
import Ir.values.BasicBlock;
import Ir.values.BuildFactory;
import Ir.values.Value;

public class BinaryIns extends Instruction {

    public BinaryIns(BasicBlock basicBlock, IROp op, Value left, Value right) {
        super(VoidType.voidType, op, basicBlock);
        boolean isLeftI1 = left.getType() instanceof IntegerType && ((IntegerType) left.getType()).isIx(1);
        boolean isRightI1 = right.getType() instanceof IntegerType && ((IntegerType) right.getType()).isIx(1);
        boolean isLeftI32 = left.getType() instanceof IntegerType && ((IntegerType) left.getType()).isIx(32);
        boolean isRightI32 = right.getType() instanceof IntegerType && ((IntegerType) right.getType()).isIx(32);
        if (isLeftI1 && isRightI32) {
            addOperands(BuildFactory.getInstance().buildZext(left, basicBlock), right);
        } else if (isLeftI32 && isRightI1) {
            addOperands(left, BuildFactory.getInstance().buildZext(right, basicBlock));
        } else {
            addOperands(left, right);
        }
        this.setType(this.getOperands().get(0).getType());
        if (isCond()) { // 是条件表达式
            this.setType(IntegerType.i1);
        }
        this.setName("%" + reg_num++);
    }

    private void addOperands(Value left, Value right) {
        this.addOperand(left);
        this.addOperand(right);
    }

    private boolean isCond(){
        return switch (this.getOp()) {
            case Lt, Le, Gt, Ge, Eq, Ne -> true;
            default -> false;
        };
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
            case Shl:
                s += "shl i32 ";
                break;
            case Shr:
                s += "ashr i32 ";
                break;
            case And:
                s += "and " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Or:
                s += "or " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Lt:
                s += "icmp slt " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Le:
                s += "icmp sle " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Ge:
                s += "icmp sge " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Gt:
                s += "icmp sgt " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Eq:
                s += "icmp eq " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Ne:
                s += "icmp ne " + this.getOperands().get(0).getType().toString() + " ";
                break;
            default:
                break;
        }
        return s + this.getOperands().get(0).getName() + ", " + this.getOperands().get(1).getName();
    }
}
