package Ir.values;

import Ir.types.FunctionType;
import Ir.types.IRType;
import Ir.types.IntegerType;
import Ir.values.instructions.*;

import java.util.ArrayList;

public class BuildFactory {
    private static BuildFactory instance = new BuildFactory();

    public static BuildFactory getInstance() {
        return instance;
    }


    /**
     * 常量相关构造
     */
    public ConstInt getConstInt(int num) {
        return new ConstInt(num);
    }


    /**
     * 变量相关构造
     */
    public GlobalVar buildGlobalVar(String name, IRType type, boolean is_const, Value value) {
        return new GlobalVar(name, type, is_const, value);
    }
    public AllocaIns buildVar(BasicBlock block, Value value, boolean is_const, IRType type) {
        AllocaIns ins = new AllocaIns(block, is_const, type);
        ins.addInsToBlock(block);
        if (value!=null){ // 有初始值
            buildStore(block, ins, value);
        }
        return ins;
    }


    public StoreIns buildStore(BasicBlock block, Value pointer, Value value) {
        StoreIns ins = new StoreIns(block, pointer, value);
        ins.addInsToBlock(block);
        return ins;
    }

    public LoadIns buildLoad(BasicBlock block, Value pointer) {
        LoadIns ins = new LoadIns(block, pointer);
        ins.addInsToBlock(block);
        return ins;
    }

    public CallIns buildCall(BasicBlock block, Function func, ArrayList<Value> args) {
        CallIns ins = new CallIns(block, func, args);
        ins.addInsToBlock(block);
        return ins;
    }





    /**
     * 函数相关Value构造
     */
    public Function buildFunction(String name, IRType ret, ArrayList<IRType>para_type){
        return new Function(name, getFuncType(ret, para_type),false);
    }
    public Function buildLibraryFunction(String name, IRType ret, ArrayList<IRType>para_type){
        return new Function(name, getFuncType(ret, para_type),true);
    }
    public FunctionType getFuncType(IRType ret, ArrayList<IRType>para_type) { // 返回一个FuncType，包含返回值和参数
        return new FunctionType(ret, para_type);
    }
    public ArrayList<Value> getFuncArgs(Function func) {
        return func.getArguments();
    }


    public RetIns buildReturn(BasicBlock block) {
        RetIns ins = new RetIns(block);
        ins.addInsToBlock(block);
        return ins;
    }
    public RetIns buildReturn(BasicBlock block, Value ret) {
        RetIns ins = new RetIns(block, ret);
        ins.addInsToBlock(block);
        return ins;
    }


    /**
     * 构建基本块
     */
    public BasicBlock buildBasicBlock(Function func) {
        return new BasicBlock(func);
    }


    /**
     * 构建二元表达式
     */
    public BinaryIns buildBinary(BasicBlock block, IROp op, Value left, Value right) {
        BinaryIns binary_ins = new BinaryIns(block, op, left, right);
        // TODO: op is And or Or
        binary_ins.addInsToBlock(block);
        return binary_ins;
    }


    /**
     * 若缺少ret，补充之
     */
    public void checkBlockEnd(BasicBlock block) {
        IRType ret_type = ((FunctionType) block.getNode().getParent().getValue().getType()).getRet_type();
        if (!block.getIns().isEmpty()) {
            Value lastInst = block.getIns().getEnd().getValue();
            if (lastInst instanceof RetIns || lastInst instanceof BrIns) {
                return;
            }
        }
        if (ret_type instanceof IntegerType) {
            buildReturn(block, ConstInt.ZERO);
        } else {
            buildReturn(block);
        }
    }

}
