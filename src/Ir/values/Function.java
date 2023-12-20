package Ir.values;

import DataStructure.IList;
import DataStructure.INode;
import Ir.IRModule;
import Ir.optimization.LoopInfo;
import Ir.types.FunctionType;
import Ir.types.IRType;

import java.util.*;

public class Function extends Value {
    private IList<BasicBlock, Function> list;
    private INode<Function, IRModule> node;
    private ArrayList<Argument> arguments;
    private ArrayList<Function> preds; // predecessors
    private ArrayList<Function> succs; // successors
    private boolean is_lib_func;

    private boolean NotAllLibSucc = false;
    private final LoopInfo loopInfo = new LoopInfo(this);
    private Map<BasicBlock, BasicBlock> idom = new HashMap<>();
    private Map<BasicBlock, Set<BasicBlock>> dom = new HashMap<>();
    private Map<BasicBlock, List<BasicBlock>> idoms = new HashMap<>();





    public Function(String name, IRType type, boolean isLibraryFunc) {
        super(name, type);
        reg_num = 0;
        this.list = new IList<>(this);
        this.node = new INode<>(this);
        this.arguments = new ArrayList<>();
        this.preds = new ArrayList<>();
        this.succs = new ArrayList<>();
        this.is_lib_func = isLibraryFunc;
        for (IRType param : ((FunctionType) type).getParam_type()){
            arguments.add(new Argument(param,((FunctionType) type).getParam_type().indexOf(param), is_lib_func));
        }
        this.node.insertAtEnd(IRModule.getInstance().getFunctions());
    }

    public IList<BasicBlock, Function> getList() {
        return list;
    }
    public LoopInfo getLoopInfo() {
        return loopInfo;
    }
    public Map<BasicBlock, Set<BasicBlock>> getDom() {
        return dom;
    }

    public ArrayList<Value> getArguments() {
        return new ArrayList<Value>(arguments);
    }
    public boolean isLibFunc() {
        return is_lib_func;
    }

    public void addPred(Function pred){
        preds.add(pred);
    }
    public void addSucc(Function succ) {
        succs.add(succ);
        if (!succ.isLibFunc())
            NotAllLibSucc = true;
    }

    public void setDom(Map<BasicBlock, Set<BasicBlock>> dom) {
        this.dom = dom;
    }
    public void setIdom(Map<BasicBlock, BasicBlock> idom) {
        this.idom = idom;
    }
    public void setIdoms(Map<BasicBlock, List<BasicBlock>> idoms) {
        this.idoms = idoms;
    }
    public Map<BasicBlock, List<BasicBlock>> getIdoms() {
        return idoms;
    }


    public void refreshArgReg() {
        for (Argument arg : arguments) {
            arg.setName("%" + reg_num++);
        }
    }

    public void computeSimpLoopInfo() {
        loopInfo.computeLoopInfo(this);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(((FunctionType) this.getType()).getRet_type()).append(" @").append(this.getName()).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            s.append(arguments.get(i).getType()).append(" ").append(arguments.get(i).getName());
            if (i != arguments.size() - 1) {
                s.append(", ");
            }
        }
        s.append(")");
        return s.toString();
    }
}
