package Ir.values;

import DataStructure.IList;
import DataStructure.INode;
import Ir.types.FunctionType;
import Ir.types.LabelType;
import Ir.types.VoidType;
import Ir.values.instructions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基本块
 *
 * @author Stevex
 * @date 2023/11/21
 */
public class BasicBlock extends Value {
    private IList<Instruction, BasicBlock> ins;
    private INode<BasicBlock, Function> node;
    private ArrayList<BasicBlock> pred; // 前驱基本块
    private ArrayList<BasicBlock> succ; // 后继基本块
    private int domLevel;

    public BasicBlock(Function func) {
        super(String.valueOf(reg_num++), new LabelType());
        this.ins = new IList<>(this);
        this.node = new INode<>(this);
        this.pred = new ArrayList<>();
        this.succ = new ArrayList<>();
        this.node.insertAtEnd(func.getList());
    }

    public IList<Instruction, BasicBlock> getIns() {
        return ins;
    }

    public INode<BasicBlock, Function> getNode() {
        return node;
    }

    public String getLabelName() {
        return "label_" + getId();
    }

    public ArrayList<BasicBlock> getPredecessors() {
        return pred;
    }
    public ArrayList<BasicBlock> getSuccessors() {
        return succ;
    }
    public int getLoopDepth() {
        return node.getParent().getValue().getLoopInfo().getLoopDepth(this);
    }
    public void setSuccessors(ArrayList<BasicBlock> successors) {
        this.succ = successors;
    }
    public void setDomLevel(int domLevel) {
        this.domLevel = domLevel;
    }
    public void addPredecessor(BasicBlock block){
        pred.add(block);
    }
    public void addSuccessor(BasicBlock block){
        succ.add(block);
    }

    public void refreshReg() {
        for (INode<Instruction, BasicBlock> node : this.ins) { // TODO:MemPhi & LoadDepInst
//            inst instanceof AliasAnalysis.MemPhi ||
//            inst instanceof AliasAnalysis.LoadDepInst ||
            Instruction ins = node.getValue();
            if (!(ins instanceof StoreIns || ins instanceof BrIns || ins instanceof RetIns ||
                    (ins instanceof CallIns &&
                            ((FunctionType) ins.getOperands().get(0).getType()).getRet_type() instanceof VoidType))){
                ins.setName("%"+reg_num++);
            }
        }
    }

    public void removeSelf() {
        for (BasicBlock bb : this.getPredecessors()) {
            bb.getSuccessors().removeIf(basicBlock -> basicBlock.equals(this));
        }
        for (BasicBlock bb : this.getSuccessors()) {
            bb.getPredecessors().forEach(pred -> {
                if (pred.equals(this)) {
                    removeBasicBlockSucc(this, bb);
                }
            });
            bb.getPredecessors().removeIf(basicBlock -> basicBlock.equals(this));
        }

        for (INode<Instruction, BasicBlock> instNode : getIns()) {
            Instruction inst = instNode.getValue();
            inst.removeUseFromOperands();
        }
        this.getNode().removeFromList();
    }

    public void removeBasicBlockSucc(BasicBlock pred, BasicBlock succ) {
        Set<Integer> idx = new HashSet<>();
        idx.add(succ.getPredecessors().indexOf(pred));
        INode<Instruction, BasicBlock> instNode = succ.getIns().getBegin();
        while (instNode != null) {
            INode<Instruction, BasicBlock> ninstNode = instNode.getNext();
            Instruction inst = instNode.getValue();
            if (!(inst instanceof PhiIns)) {
                break;
            }

            if (inst.getOperands().size() == 1) {
                inst.replaceUsedWith(inst.getOperands().get(0));
                inst.removeUseFromOperands();
                instNode.removeFromList();
            } else {
                inst.removeNumberOperand(idx);
            }

            instNode = ninstNode;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (INode<Instruction, BasicBlock> instruction : this.ins){
            sb.append("\t").append(instruction.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}
