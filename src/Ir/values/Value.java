package Ir.values;

import Ir.IRModule;
import Ir.types.IRType;

import java.util.ArrayList;

public class Value {
    private IRModule module = IRModule.getInstance();
    private String id; // LLVM中Value的唯一编号
    private String name;
    private IRType type;
    private ArrayList<Use> uses_list;
    public static int reg_num = 0;
    public static int unique_id_count = 0;

    public Value(String name, IRType type) {
        id = "uid" + ++unique_id_count;
        this.name = name;
        this.type = type;
        this.uses_list = new ArrayList<Use>();
    }

    public void addUse(Use use) {
        this.uses_list.add(use);
    }

    public void removeUseByUser(User user) {
        ArrayList<Use> tmp_list = new ArrayList<>(uses_list);
        for (Use use : uses_list){
            if (use.getUser().equals(user)){
                tmp_list.remove(use);
            }
        }
        this.uses_list = tmp_list;
    }


    public IRModule getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public IRType getType() {
        return type;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setType(IRType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }

}
