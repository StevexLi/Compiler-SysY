package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

/**
 * LAndExp → EqExp | LAndExp '&&' EqExp // 1.EqExp 2.&& 均需覆盖
 * @author Stevex
 * @date 2023/10/14
 */
public class LAndExp extends NonTerminal {
    ArrayList<ASTNode> EqExp_list = new ArrayList<>();
    ArrayList<ASTNode> EqExp = new ArrayList<>();

    /**
     * LAndExp 逻辑与表达式
     * 改写为 LAndExp → EqExp { '&&' EqExp}
     *
     * @throws Exception 异常
     */
    LAndExp() throws Exception {
        this.nt_type = NonTerminalType.LANDEXP;
        ASTNode node2 = new ASTNode(new Token(new EqExp()));
        EqExp_list.add(node2);
        while (Parser.now.equalLexType(LexType.AND)) {
            EqExp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            EqExp_list.add(new ASTNode(new Token(new EqExp())));
        }
        int i = EqExp_list.size();
        if (i == 1){ // EqExp
            EqExp.add(node2);
            setFirstchild(node2);
        } else { // LAndExp '&&' EqExp
            EqExp.add(new ASTNode(new Token(new LAndExp(EqExp_list))));
            EqExp.add(EqExp_list.get(i-2));
            EqExp.add(EqExp_list.get(i-1));
            setFirstchild(EqExp.get(0));
            EqExp.get(0).setNextSibling(EqExp.get(1));
            EqExp.get(1).setNextSibling(EqExp.get(2));
        }
    }

    /**
     * LAndExp
     * 递归建造LAndExp节点
     *
     * @param EqExp_list_prev 上一层LAndExp结点的改写后文法序列
     * @throws Exception 异常
     */
    LAndExp(ArrayList<ASTNode> EqExp_list_prev) throws Exception {
        this.nt_type = NonTerminalType.LANDEXP;
        for (int i=0;i<EqExp_list_prev.size()-2;i++){
            EqExp_list.add(EqExp_list_prev.get(i));
        }
        int i = EqExp_list.size();
        if (i == 1) {
            EqExp.add(EqExp_list.get(0));
            setFirstchild(EqExp.get(0));
        } else {
            EqExp.add(new ASTNode(new Token(new LAndExp(EqExp_list))));
            EqExp.add(EqExp_list.get(i-2));
            EqExp.add(EqExp_list.get(i-1));
            setFirstchild(EqExp.get(0));
            EqExp.get(0).setNextSibling(EqExp.get(1));
            EqExp.get(1).setNextSibling(EqExp.get(2));
        }
    }

    public ArrayList<Token> getEqExp_list() {
        ArrayList<Token> list = new ArrayList<>();
        for (ASTNode node : EqExp_list){
            list.add(node.getDataToken());
        }
        return list;
    }
}
