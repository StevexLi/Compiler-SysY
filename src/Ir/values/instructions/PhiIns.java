package Ir.values.instructions;

import Ir.types.IRType;
import Ir.values.BasicBlock;
import Ir.values.Value;

import java.util.List;

public class PhiIns extends MemIns {
    public PhiIns(BasicBlock basicBlock, IRType type, List<Value> values) {
        super(type, IROp.Phi, basicBlock);
        for (Value value : values) {
            this.addOperand(value);
        }
        this.setName("%" +  reg_num++);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getName()).append(" = phi ").append(getType()).append(" ");
        for (int i = 0; i < getOperands().size(); i++) {
            s.append("[ ")
                    .append(getOperands().get(i).getName())
                    .append(", %")
                    .append(this.getNode()
                            .getParent()
                            .getValue()
                            .getPredecessors()
                            .get(i)
                            .getName())
                    .append(" ]");
            if (i != getOperands().size() - 1) {
                s.append(", ");
            }
        }
        return s.toString();
    }
}
