package Ir.values;

import DataStructure.IList;
import DataStructure.INode;
import Ir.IRModule;
import Ir.types.FunctionType;
import Ir.types.IRType;

import java.util.ArrayList;

public class Function extends Value {
    private IList<BasicBlock, Function> list;
    private INode<Function, IRModule> node;
    private ArrayList<Argument> arguments;
    private ArrayList<Function> pred; // predecessors
    private ArrayList<Function> succ; // successors
    private boolean is_lib_func;


    public Function(String name, IRType type, boolean isLibraryFunc) {
        super(name, type);
        reg_num = 0;
        this.list = new IList<>(this);
        this.node = new INode<>(this);
        this.arguments = new ArrayList<>();
        this.pred = new ArrayList<>();
        this.succ = new ArrayList<>();
        this.is_lib_func = isLibraryFunc;
        for (IRType param : ((FunctionType) type).getParam_type()){
            arguments.add(new Argument(param,((FunctionType) type).getParam_type().indexOf(param), is_lib_func));
        }
        this.node.insertAtEnd(IRModule.getInstance().getFunctions());
    }

    public IList<BasicBlock, Function> getList() {
        return list;
    }
    public ArrayList<Value> getArguments() {
        return new ArrayList<Value>(arguments);
    }
    public boolean isLibFunc() {
        return is_lib_func;
    }


    public void refreshArgReg() {
        for (Argument arg : arguments) {
            arg.setName("%" + reg_num++);
        }
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
