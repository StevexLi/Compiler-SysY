package Parser;

import Lexer.LexType;
import Lexer.Token;

import java.util.ArrayList;

/**
 * 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp // 1.LAndExp 2.|| 均需覆盖
 * @author Stevex
 * @date 2023/10/14
 */
public class LOrExp extends NonTerminal {
    ArrayList<ASTNode> LAndExp_list = new ArrayList<>();
    ArrayList<ASTNode> LAndExp = new ArrayList<>();

    /**
     * LOrExp 逻辑或
     * 改写为 LOrExp → LAndExp { '||' LAndExp}
     *
     * @throws Exception 异常
     */
    LOrExp() throws Exception { // LOrExp → LAndExp | LOrExp '||' LAndExp // 1.LAndExp 2.|| 均需覆盖
        this.nt_type = NonTerminalType.LOREXP;
        ASTNode node2 = new ASTNode(new Token(new LAndExp()));
        LAndExp_list.add(node2);
        while (Parser.now.equalLexType(LexType.OR)) {
            LAndExp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            LAndExp_list.add(new ASTNode(new Token(new LAndExp())));
        }
        int i = LAndExp_list.size();
        if (i == 1){ // LAndExp
            LAndExp.add(node2);
            setFirstchild(node2);
        } else { //  LOrExp '||' LAndExp
            LAndExp.add(new ASTNode(new Token(new LOrExp(LAndExp_list))));
            LAndExp.add(LAndExp_list.get(i-2));
            LAndExp.add(LAndExp_list.get(i-1));
            setFirstchild(LAndExp.get(0));
            LAndExp.get(0).setNextSibling(LAndExp.get(1));
            LAndExp.get(1).setNextSibling(LAndExp.get(2));
        }
    }

    /**
     * 递归建造LOrExp 逻辑或节点
     *
     * @param LAndExp_list_prev 上一层LOrExp结点的改写后文法序列
     * @throws Exception 异常
     */
    LOrExp(ArrayList<ASTNode> LAndExp_list_prev) throws Exception { // LOrExp → LAndExp | LOrExp '||' LAndExp // 1.LAndExp 2.|| 均需覆盖
        this.nt_type = NonTerminalType.LOREXP;
        for (int i=0;i<LAndExp_list_prev.size()-2;i++){
            LAndExp_list.add(LAndExp_list_prev.get(i));
        }
        int i = LAndExp_list.size();
        if (i == 1) {
            LAndExp.add(LAndExp_list.get(0));
            setFirstchild(LAndExp.get(0));
        } else {
            LAndExp.add(new ASTNode(new Token(new LOrExp(LAndExp_list))));
            LAndExp.add(LAndExp_list.get(i-2));
            LAndExp.add(LAndExp_list.get(i-1));
            setFirstchild(LAndExp.get(0));
            LAndExp.get(0).setNextSibling(LAndExp.get(1));
            LAndExp.get(1).setNextSibling(LAndExp.get(2));
        }
    }
}
