package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

/**
 *  EqExp → RelExp | EqExp ('==' | '!=') RelExp // 1.RelExp 2.== 3.!= 均需
 * 覆盖
 * @author Stevex
 * @date 2023/10/14
 */
public class EqExp extends NonTerminal {
    ArrayList<ASTNode> RelExp_list = new ArrayList<>();
    ArrayList<ASTNode> RelExp = new ArrayList<>();

    /**
     * EqExp 相等性表达式
     * 改写为 EqExp → RelExp { ('==' | '!=') RelExp}
     *
     * @throws Exception 异常
     */
    EqExp() throws Exception {
        this.nt_type = NonTerminalType.EQEXP;
        ASTNode node2 = new ASTNode(new Token(new RelExp()));
        RelExp_list.add(node2);
        while (Parser.now.equalLexType(LexType.EQL)||Parser.now.equalLexType(LexType.NEQ)) {
            RelExp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            RelExp_list.add(new ASTNode(new Token(new RelExp())));
        }
        int i = RelExp_list.size();
        if (i == 1){ // RelExp
            RelExp.add(node2);
            setFirstchild(node2);
        } else { // EqExp ('==' | '!=') RelExp
            RelExp.add(new ASTNode(new Token(new EqExp(RelExp_list))));
            RelExp.add(RelExp_list.get(i-2));
            RelExp.add(RelExp_list.get(i-1));
            setFirstchild(RelExp.get(0));
            RelExp.get(0).setNextSibling(RelExp.get(1));
            RelExp.get(1).setNextSibling(RelExp.get(2));
        }
    }

    /**
     * EqExp
     * 递归建造EqExp节点
     *
     * @param RelExp_list_prev 上一层MulExp结点的改写后文法序列
     * @throws Exception 异常
     */
    EqExp(ArrayList<ASTNode> RelExp_list_prev) throws Exception {
        this.nt_type = NonTerminalType.EQEXP;
        for (int i=0;i<RelExp_list_prev.size()-2;i++){
            RelExp_list.add(RelExp_list_prev.get(i));
        }
        int i = RelExp_list.size();
        if (i == 1) {
            RelExp.add(RelExp_list.get(0));
            setFirstchild(RelExp.get(0));
        } else {
            RelExp.add(new ASTNode(new Token(new EqExp(RelExp_list))));
            RelExp.add(RelExp_list.get(i-2));
            RelExp.add(RelExp_list.get(i-1));
            setFirstchild(RelExp.get(0));
            RelExp.get(0).setNextSibling(RelExp.get(1));
            RelExp.get(1).setNextSibling(RelExp.get(2));
        }
    }

    public ArrayList<Token> getRelExp_list() {
        ArrayList<Token> list = new ArrayList<>();
        for (ASTNode node : RelExp_list){
            list.add(node.getDataToken());
        }
        return list;
    }
}
