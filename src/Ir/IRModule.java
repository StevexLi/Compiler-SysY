package Ir;

import Ir.values.*;
import DataStructure.*;
import Ir.values.instructions.Instruction;

import java.util.ArrayList;
import java.util.HashMap;

public class IRModule {
    private static IRModule instance = new IRModule();
    public static IRModule getInstance() {
        return instance;
    }
    private ArrayList<GlobalVar> global_vars;
    private IList<Function, IRModule> functions;
    private HashMap<Integer, Instruction> instructions;

    public IRModule() {
        this.global_vars = new ArrayList<GlobalVar>();
        this.functions = new IList<>(this);
        this.instructions = new HashMap<>();
    }

    public IList<Function, IRModule> getFunctions() {
        return this.functions;
    }

    public void addGlobalVar(GlobalVar global_var) {
        global_vars.add(global_var);
    }
    public void addInstruction(int handler, Instruction ins){
        this.instructions.put(handler,ins);
    }


    public void refreshRegNum() {
        for (INode<Function, IRModule> func : functions){
            Value.reg_num = 0;
            func.getValue().refreshArgReg();
            if (!func.getValue().isLibFunc()){ // 非库函数
                for (INode<BasicBlock, Function> block : func.getValue().getList()){
                    if (block.getValue().getIns().isEmpty()){
                        BuildFactory.getInstance().checkBlockEnd(block.getValue());
                    }
                    block.getValue().setName(String.valueOf(Value.reg_num++));
                    block.getValue().refreshReg();
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb_gyj = new StringBuilder();
        for (GlobalVar g_var : global_vars){
            sb_gyj.append(g_var.toString()).append("\n");
        }
        if (!global_vars.isEmpty()){
            sb_gyj.append("\n");
        }
        refreshRegNum();
        for (INode<Function, IRModule> func : functions) {
            if (func.getValue().isLibFunc()){ // 是库函数
                sb_gyj.append("declare ").append(func.getValue().toString()).append("\n");
            } else {
                sb_gyj.append("\ndefine dso_local ").append(func.getValue().toString()).append("{\n");
                for (INode<BasicBlock, Function> block : func.getValue().getList()){
                    if (block != func.getValue().getList().getBegin())
                        sb_gyj.append("\n");
                    sb_gyj.append(block.getValue().getName()).append(":\n").append(block.getValue().toString());
                }
                sb_gyj.append("}\n");
            }
        }
        sb_gyj.append("\n; this is llvm_ir.txt");
        return sb_gyj.toString();
    }
}
