package BackEnd;

import java.util.*;

import Ir.IRModule;
import Ir.types.*;
import Ir.values.*;
import Ir.values.*;
import Ir.values.instructions.*;
import Ir.values.instructions.AllocaIns;
import Ir.values.instructions.GEPIns;
import Ir.values.instructions.LoadIns;
import Ir.values.instructions.StoreIns;
import Ir.values.instructions.BrIns;
import Ir.values.instructions.CallIns;
import Ir.values.instructions.RetIns;
import Ir.optimization.OptimizationLevel;
import DataStructure.INode;
import DataStructure.Triple;

public class MIPSGenerator {
    private static final MIPSGenerator instance = new MIPSGenerator();
    private IRModule irModule;

    private MIPSGenerator() {
    }

    public static MIPSGenerator getInstance() {
        return instance;
    }

    public void loadIR() {
        irModule = IRModule.getInstance();
    }

    public static StringBuilder sb = new StringBuilder();

    public String genMips() {
        sb.append(".data\n");
        for (GlobalVar gv : irModule.getGlobalVars()) {
            sb.append("\n# " + gv + "\n\n");
            if (gv.isString()) {
                ConstString constString = (ConstString) gv.getValue();
                sb.append(gv.getUniqueName() + ": .asciiz " + constString.getName() + "\n");
            } else if (gv.isInt()) {
                getGp(gv.getUniqueName(), gv);
                sb.append(gv.getUniqueName() + ": .word " + ((ConstInt) gv.getValue()).getValue() + "\n");
            } else if (gv.isArray()) {
                ConstArray constArray = (ConstArray) gv.getValue();
                getGp(gv.getUniqueName(), gv);
                PointerType pt = (PointerType) gv.getType();
                sb.append(gv.getUniqueName() + ": ");
                if (constArray.isInit()) {
                    sb.append(".word ");
                    // 数组初值
                    int capacity = ((ArrayType) pt.getTargetType()).getCapacity();
                    for (int i = 0; i < capacity; i++) {
                        sb.append(String.valueOf(((ConstInt) (constArray).get1DArray().get(i)).getValue()));
                        if (i != capacity - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("\n");
                } else {
                    sb.append(".space " + ((ArrayType) pt.getTargetType()).getCapacity() * 4 + "\n");
                }
            }
        }
        for (INode<Function, IRModule> funcEntry : irModule.getFunctions()) {
            Function function = funcEntry.getValue();
            if (function.isLibFunc()) {
                if (Objects.equals(function.getName(), "getint"))
                    sb.append("\n.macro GETINT()\nli $v0, 5\nsyscall\n.end_macro\n");
                else if (Objects.equals(function.getName(), "putint"))
                    sb.append("\n.macro PUTINT()\nli $v0, 1\nsyscall\n.end_macro\n");
                else if (Objects.equals(function.getName(), "putch"))
                    sb.append("\n.macro PUTCH()\nli $v0, 11\nsyscall\n.end_macro\n");
                else if (Objects.equals(function.getName(), "putstr"))
                    sb.append("\n.macro PUTSTR()\nli $v0, 4\nsyscall\n.end_macro\n");
            }
        }
        sb.append("\n.text\n");
        sb.append("\njal main\n");
        sb.append("\nj return\n\n");

        for (INode<Function, IRModule> funcEntry : irModule.getFunctions()) {
            Function function = funcEntry.getValue();
            if (function.isLibFunc()) {
                continue;
            }
            sb.append("\n" + function.getName() + ":\n");
            rec = function.getArguments().size();
            for (int i = 0; rec > 0; i++) {
                rec--;
                load("$t0", "$sp", 4 * rec);
                getSp(function.getArguments().get(i).getUniqueName(), function.getArguments().get(i));
                store("$t0", function.getArguments().get(i).getUniqueName());
            }
            rec = 0;
            for (INode<BasicBlock, Function> blockEntry : function.getList()) {
                BasicBlock basicBlock = blockEntry.getValue();
                sb.append("\n" + basicBlock.getLabelName() + ":\n");
                for (INode<Instruction, BasicBlock> instEntry : basicBlock.getIns()) {
                    Instruction ir = instEntry.getValue();
                    sb.append("\n# " + ir.toString() + "\n\n");
                    if (!(ir instanceof AllocaIns)) {
                        getSp(ir.getUniqueName(), ir);
                    }
                    translate(ir);
                }
            }
        }
        sb.append("\nreturn:\n");
        return sb.toString();
    }


    private Map<String, Triple<String, Integer, Value>> mem = new HashMap<>();
    int spOff = 0, rec = 0;

    private void getGp(String name, Value value) {
        if (mem.containsKey(name)) {
            return;
        }
        mem.put(name, new Triple<>("$gp", 0, value));
    }

    private void getSp(String name, Value value) {
        if (mem.containsKey(name)) {
            return;
        }
        spOff -= 4;
        mem.put(name, new Triple<>("$sp", spOff, value));
    }

    private void getSpArray(String name, int offset, Value value) {
        if (mem.containsKey(name)) {
            return;
        }
        getSp(name, value);
        spOff -= offset;
        sb.append("addu $t0, $sp, " + spOff + "\n");
        store("$t0", name);
    }

    private void translate(Instruction ir) {
        if (ir instanceof BinaryIns) parseBinary((BinaryIns) ir);
        else if (ir instanceof CallIns) parseCall((CallIns) ir);
        else if (ir instanceof RetIns) parseRet((RetIns) ir);
        else if (ir instanceof AllocaIns) parseAlloca((AllocaIns) ir);
        else if (ir instanceof LoadIns) parseLoad((LoadIns) ir);
        else if (ir instanceof StoreIns) parseStore((StoreIns) ir);
        else if (ir instanceof GEPIns) parseGEP((GEPIns) ir);
        else if (ir instanceof BrIns) parseBr((BrIns) ir);
        else if (ir instanceof ConvIns) parseConv((ConvIns) ir);
        // else if (ir instanceof PhiIns) parsePhi((PhiIns) ir);
    }

    private void parseBinary(BinaryIns b) {
        if (b.isAdd()) calc(b, "addu", 0);
        else if (b.isSub()) calc(b, "subu", 1);
        else if (b.isMul()) {
            if (!OptimizationLevel.MulAndDivOptimization) {
                calc(b, "mul", 0);
                return;
            }
            Value left = b.getOperand(0), right = b.getOperand(1);
            boolean isLeftConst = left instanceof ConstInt, isRightConst = right instanceof ConstInt;
            int leftValue = isLeftConst ? ((ConstInt) left).getValue() : 0;
            int rightValue = isRightConst ? ((ConstInt) right).getValue() : 0;
            int leftAbs = leftValue >= 0 ? leftValue : -leftValue;
            int rightAbs = rightValue >= 0 ? rightValue : -rightValue;
            if (isLeftConst && leftValue == 0) {
                optimizeMul(right, (ConstInt) left, b, false);
            } else if (isRightConst && rightValue == 0) {
                optimizeMul(left, (ConstInt) right, b, false);
            } else if (isLeftConst && leftValue == 1) {
                optimizeMul(right, (ConstInt) left, b, false);
            } else if (isRightConst && rightValue == 1) {
                optimizeMul(left, (ConstInt) right, b, false);
            } else if (isLeftConst && leftValue == -1) {
                optimizeMul(right, (ConstInt) left, b, false);
            } else if (isRightConst && rightValue == -1) {
                optimizeMul(left, (ConstInt) right, b, false);
            } else if (isLeftConst && (leftAbs & (leftAbs - 1)) == 0) {
                optimizeMul(right, (ConstInt) left, b, false);
            } else if (isRightConst && (rightAbs & (rightAbs - 1)) == 0) {
                optimizeMul(left, (ConstInt) right, b, false);
            } else if (isLeftConst && ((leftAbs - 1) & (leftAbs - 2)) == 0) {
                optimizeMul(right, (ConstInt) left, b, false);
            } else if (isRightConst && ((rightAbs - 1) & (rightAbs - 2)) == 0) {
                optimizeMul(left, (ConstInt) right, b, false);
            } else if (isLeftConst && ((leftAbs + 1) & leftAbs) == 0) {
                optimizeMul(right, (ConstInt) left, b, false);
            } else if (isRightConst && ((rightAbs + 1) & rightAbs) == 0) {
                optimizeMul(left, (ConstInt) right, b, false);
            } else {
                calc(b, "mul", 0);
            }
        } else if (b.isDiv()) {
            if (!OptimizationLevel.MulAndDivOptimization) {
                calc(b, "div", 1);
                return;
            }
            if (b.getOperand(1) instanceof ConstInt) {
                optimizeDiv(b.getOperand(0), ((ConstInt) b.getOperand(1)), b, false);
            } else {
                calc(b, "div", 1);
            }
        } else if (b.isMod()) {
            if (!OptimizationLevel.MulAndDivOptimization) {
                calc(b, "rem", 1);
                return;
            }
            if (b.getOperand(1) instanceof ConstInt) {
                optimizeMod(b.getOperand(0), (ConstInt) b.getOperand(1), b);
            } else {
                calc(b, "rem", 1);
            }
        } else if (b.isShl()) calc(b, "sll", 1);
        else if (b.isShr()) calc(b, "srl", 1);
        else if (b.isAnd()) calc(b, "and", 0);
        else if (b.isOr()) calc(b, "or", 0);
        else if (b.isLe()) calc(b, "sle", 1);
        else if (b.isLt()) calc(b, "slt", 2);
        else if (b.isGe()) calc(b, "sge", 1);
        else if (b.isGt()) calc(b, "sgt", 2);
        else if (b.isEq()) calc(b, "seq", 0);
        else if (b.isNe()) calc(b, "sne", 0);
        else if (b.isNot()) {
            load("$t0", b.getOperand(0).getUniqueName());
            sb.append("not $t0, $t0\n");
            store("$t0", b.getUniqueName());
        }

    }

    private void optimizeMul(Value operand, ConstInt immValue, Instruction b, boolean isMod) {
        int imm = immValue.getValue();
        int abs = imm >= 0 ? imm : -imm;
        if (imm == 0) {
            load("$t0", "0");
        } else {
            if (!isMod) {
                load("$t0", operand.getUniqueName());
            }
            if (imm == 1) {
            } else if (imm == -1) {
                sb.append("negu $t0, $t0\n");
            } else if ((abs & (abs - 1)) == 0) {
                sb.append("sll $t0, $t0, " + getCTZ(abs) + "\n");
                if (imm <= 0) {
                    sb.append("negu $t0, $t0\n");
                }
            } else if (((abs - 1) & (abs - 2)) == 0) {
                sb.append("sll $t1, $t0, " + getCTZ(abs) + "\n");
                sb.append("addu $t0, $t0, $t1\n");
                if (imm <= 0) {
                    sb.append("negu $t0, $t0\n");
                }
            } else if (((abs + 1) & abs) == 0) {
                sb.append("sll $t1, $t0, " + (getCTZ(abs) + 1) + "\n");
                if (imm > 0) {
                    sb.append("subu $t0, $t1, $t0\n");
                } else {
                    sb.append("subu $t0, $t0, $t1\n");
                }
            } else {
                sb.append("mul $t0, $t0, " + imm + "\n");
            }
        }
        if (!isMod) {
            store("$t0", b.getUniqueName());
        }
    }

    private void optimizeDiv(Value operand, ConstInt immValue, BinaryIns b, boolean isMod) {
        int imm = immValue.getValue();
        if (imm == 1) {
            load("$t0", operand.getUniqueName());
        } else if (imm == -1) {
            load("$t0", operand.getUniqueName());
            sb.append("negu $t0, $t0\n");
        } else {
            int abs = imm >= 0 ? imm : -imm;
            load("$t0", operand.getUniqueName()); // n
            if ((abs & (abs - 1)) == 0) {
                // (n + ((n >> (31)) >>> (32 - l))) >> l
                sb.append("sra $t1, $t0, 31\n"); // n >> 31
                int l = getCTZ(abs);
                sb.append("srl $t1, $t1, " + (32 - l) + "\n"); // (n >> 31) >>> (32 - l)
                sb.append("addu $t0, $t0, $t1\n"); // n + ((n >> 31) >>> (32 - l))
                sb.append("sra $t0, $t0, " + l + "\n"); // (n + ((n >> 31) >>> (32 - l))) >> l
            } else {
                Triple<Long, Integer, Integer> multiplier = chooseMultiplier(abs, 31);
                long m = multiplier.getFirst();
                int sh = multiplier.getSecond();
                if (m < 2147483648L) {
                    load("$t1", String.valueOf(m));
                    sb.append("mult $t0, $t1\n");
                    sb.append("mfhi $t2\n");
                } else {
                    load("$t1", String.valueOf((m - (1L << 32))));
                    sb.append("mult $t0, $t1\n");
                    sb.append("mfhi $t2\n");
                    sb.append("addu $t2, $t2, $t0\n");
                }
                sb.append("sra $t2, $t2, " + sh + "\n");
                sb.append("srl $t1, $t0, 31\n");
                sb.append("addu $t0, $t2, $t1\n");
            }
            if (imm < 0) {
                sb.append("negu $t0, $t0\n");
            }
        }
        if (!isMod) {
            store("$t0", b.getUniqueName());
        }
    }

    private void optimizeMod(Value operand, ConstInt immValue, BinaryIns b) {
        int imm = immValue.getValue();
        if (imm == 1 || imm == -1) {
            load("$t0", "0");
        } else {
            optimizeDiv(operand, immValue, b, true);
            sb.append("\n");
            optimizeMul(b, immValue, b, true);
            sb.append("\n");
            load("$t1", operand.getUniqueName());
            sb.append("subu $t0, $t1, $t0\n");
        }
        store("$t0", b.getUniqueName());
    }

    public int getCTZ(int num) {
        int r = 0;
        num >>>= 1;
        while (num > 0) {
            r++;
            num >>>= 1;
        }
        return r; // 0 - 31
    }

    private Triple<Long, Integer, Integer> chooseMultiplier(int d, int prec) {
        long nc = (1L << prec) - ((1L << prec) % d) - 1;
        long p = 32;
        while ((1L << p) <= nc * (d - (1L << p) % d)) {
            p++;
        }
        long m = (((1L << p) + (long) d - (1L << p) % d) / (long) d);
        long n = ((m << 32) >>> 32);
        return new Triple<>(n, (int) (p - 32), 0);
    }

    private void calc(BinaryIns b, String op, int type) {
        if (type == 0 && b.getOperand(0) instanceof ConstInt) {
            load("$t0", b.getOperand(1).getUniqueName());
            sb.append(op + " $t0, $t0, " + ((ConstInt) b.getOperand(0)).getValue() + "\n");
            store("$t0", b.getUniqueName());
            return;
        }
        if (type <= 1 && b.getOperand(1) instanceof ConstInt) {
            load("$t0", b.getOperand(0).getUniqueName());
            sb.append(op + " $t0, $t0, " + ((ConstInt) b.getOperand(1)).getValue() + "\n");
            store("$t0", b.getUniqueName());
            return;
        }
        load("$t0", b.getOperand(0).getUniqueName());
        load("$t1", b.getOperand(1).getUniqueName());
        sb.append(op + " $t0, $t0, $t1\n");
        store("$t0", b.getUniqueName());
    }

    private void parseCall(CallIns callIns) {
        Function function = callIns.getCalledFunction();
        if (function.isLibFunc()) {
            if (Objects.equals(function.getName(), "getint")) {
                sb.append("GETINT()\n");
                store("$v0", callIns.getUniqueName());
            } else if (Objects.equals(function.getName(), "putint")) {
                load("$a0", callIns.getOperand(1).getUniqueName());
                sb.append("PUTINT()\n");
            } else if (Objects.equals(function.getName(), "putch")) {
                load("$a0", callIns.getOperand(1).getUniqueName());
                sb.append("PUTCH()\n");
            } else if (Objects.equals(function.getName(), "putstr")) {
                sb.append("PUTSTR()\n");
            }
        } else {
            store("$ra", "$sp", spOff - 4);
            rec = 1;
            int argSize = callIns.getCalledFunction().getArguments().size();
            for (int i = 1; i <= argSize; i++) {
                rec++;
                load("$t0", callIns.getOperand(i).getUniqueName());
                store("$t0", "$sp", spOff - rec * 4);
            }
            sb.append("addu $sp, $sp, " + (spOff - rec * 4) + "\n");
            sb.append("jal " + function.getName() + "\n");
            sb.append("addu $sp, $sp, " + (-spOff + rec * 4) + "\n");
            load("$ra", "$sp", spOff - 4);
            if (!(((FunctionType) function.getType()).getRet_type() instanceof VoidType)) {
                store("$v0", callIns.getUniqueName());
            }
        }
    }

    private void parseRet(RetIns ret) {
        if (!ret.isVoid()) {
            load("$v0", ret.getOperand(0).getUniqueName());
        }
        sb.append("jr $ra\n");
    }

    private void parseAlloca(AllocaIns allocaIns) {
        if (allocaIns.getAlloca_type() instanceof PointerType) {
            PointerType pointerType = (PointerType) allocaIns.getAlloca_type();
            if (pointerType.getTargetType() instanceof IntegerType) {
                getSp(allocaIns.getUniqueName(), allocaIns);
            } else if (pointerType.getTargetType() instanceof ArrayType) {
                ArrayType arrayType = (ArrayType) pointerType.getTargetType();
                getSpArray(allocaIns.getUniqueName(), 4 * arrayType.getCapacity(), allocaIns);
            }
        } else if (allocaIns.getAlloca_type() instanceof IntegerType) {
            getSp(allocaIns.getUniqueName(), allocaIns);
        } else if (allocaIns.getAlloca_type() instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) allocaIns.getAlloca_type();
            getSpArray(allocaIns.getUniqueName(), 4 * arrayType.getCapacity(), allocaIns);
        }
    }

    private void parseLoad(LoadIns loadIns) {
        if (loadIns.getOperand(0) instanceof GEPIns) {
            load("$t0", loadIns.getOperand(0).getUniqueName());
            load("$t1", "$t0", 0);
            store("$t1", loadIns.getUniqueName());
        } else {
            load("$t0", loadIns.getOperand(0).getUniqueName());
            store("$t0", loadIns.getUniqueName());
        }
    }

    private void parseStore(StoreIns storeIns) {
        if (storeIns.getOperand(1) instanceof GEPIns) {
            load("$t0", storeIns.getOperand(0).getUniqueName());
            load("$t1", storeIns.getOperand(1).getUniqueName());
            store("$t0", "$t1", 0);
        } else {
            load("$t0", storeIns.getOperand(0).getUniqueName());
            store("$t0", storeIns.getOperand(1).getUniqueName());
        }
    }

    private void parseGEP(GEPIns gepIns) {
        PointerType pt = (PointerType) gepIns.getPointer().getType();
        if (pt.isString()) {
            sb.append("la $a0, " + gepIns.getPointer().getGlobalName() + "\n");
            return;
        }
        int offsetNum;
        List<Integer> dims;
        if (pt.getTargetType() instanceof ArrayType) {
            offsetNum = gepIns.getOperands().size() - 1;
            dims = ((ArrayType) pt.getTargetType()).getDims();
        } else {
            offsetNum = 1;
            dims = new ArrayList<>();
        }
        load("$t2", gepIns.getPointer().getUniqueName()); // arr
        int lastOff = 0;
        for (int i = 1; i <= offsetNum; i++) {
            int base = 4;
            if (pt.getTargetType() instanceof ArrayType) {
                for (int j = i - 1; j < dims.size(); j++) {
                    base *= dims.get(j);
                }
            }
            if (gepIns.getOperand(i).isNumber()) {
                int dimOff = gepIns.getOperand(i).getNumber() * base;
                lastOff += dimOff;
                if (i == offsetNum) {
                    if (lastOff != 0) {
                        sb.append("addu $t2, $t2, " + lastOff + "\n");
                    }
                    store("$t2", gepIns.getUniqueName());
                }
            } else {
                if (lastOff != 0) {
                    sb.append("addu $t2, $t2, " + lastOff + "\n");
                }
                load("$t0", gepIns.getOperand(i).getUniqueName()); // offset
                optimizeMul(gepIns.getOperand(i), new ConstInt(base), gepIns, true);
                sb.append("addu $t2, $t2, $t0\n");
                store("$t2", gepIns.getUniqueName());
            }
            sb.append("\n");
        }
    }

    private void parseBr(BrIns brIns) {
        if (brIns.isCondBr()) {
            load("$t0", brIns.getCond().getUniqueName());
            sb.append("beqz $t0, " + brIns.getFalseLabel().getLabelName() + "\n");
            sb.append("j " + brIns.getTrueLabel().getLabelName() + "\n");
        } else {
            sb.append("j " + brIns.getTrueLabel().getLabelName() + "\n");
        }
    }

    private void parseConv(ConvIns convIns) {
        if (convIns.getOp() == IROp.Zext) {
            load("$t0", convIns.getOperand(0).getUniqueName());
            store("$t0", convIns.getUniqueName());
        } else if (convIns.getOp() == IROp.Bitcast) {
            load("$t0", convIns.getOperand(0).getUniqueName());
            store("$t0", convIns.getUniqueName());
        }
    }

    private void load(String reg, String name) {
        if (isNumber(name)) {
            sb.append("li " + reg + ", " + name + "\n");
        } else if (mem.get(name).getThird() instanceof GlobalVar) {
            sb.append("la " + reg + ", " + name + "\n");
            if (((GlobalVar) mem.get(name).getThird()).isInt()) {
                sb.append("lw " + reg + ", 0(" + reg + ")\n");
            }
        } else {
            sb.append("lw " + reg + ", " + mem.get(name).getSecond() + "(" + mem.get(name).getFirst() + ")\n");
        }
    }

    private void load(String reg, String name, int offset) {
        sb.append("lw " + reg + ", " + offset + "(" + name + ")\n");
    }

    private void store(String reg, String name) {
        if (mem.get(name).getThird() instanceof GlobalVar) {
            sb.append("la $t1, " + name + "\n");
            if (((GlobalVar) mem.get(name).getThird()).isInt()) {
                sb.append("sw " + reg + ", 0($t1)\n");
            }
        } else {
            sb.append("sw " + reg + ", " + mem.get(name).getSecond() + "(" + mem.get(name).getFirst() + ")\n");
        }
    }

    private void store(String reg, String name, int offset) {
        sb.append("sw " + reg + ", " + offset + "(" + name + ")\n");
    }

    private boolean isNumber(String str) {
        return str.matches("-?[0-9]+");
    }
}
