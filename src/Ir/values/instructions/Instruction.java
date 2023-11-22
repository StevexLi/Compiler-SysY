package Ir.values.instructions;

import DataStructure.INode;
import Ir.types.IRType;
import Ir.values.BasicBlock;
import Ir.values.User;

public class Instruction extends User {
    private IROp op;
    private INode<Instruction, BasicBlock> node;
    private int handler;
    private static int HANDLER = 0;


    public Instruction(IRType type, IROp op, BasicBlock basicBlock){
        super("",type);
        this.op = op;
        this.node = new INode<>(this);
        this.handler = HANDLER++;
        this.getModule().addInstruction(handler, this);
    }

    public void addInsToBlock(BasicBlock block) {
        if (block.getIns().getEnd() == null || (!(block.getIns().getEnd().getValue() instanceof BrIns) && (!(block.getIns().getEnd().getValue() instanceof RetIns)))){
            this.getNode().insertAtEnd(block.getIns());
        } else {
            this.removeUseFromOperands();
        }
    }

    public IROp getOp() {
        return op;
    }

    public INode<Instruction, BasicBlock> getNode() {
        return node;
    }
}
