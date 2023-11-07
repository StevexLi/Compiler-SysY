package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

public class MulExp extends NonTerminal {
    ArrayList<ASTNode> UnaryExp_list = new ArrayList<>();
    ArrayList<ASTNode> UnaryExp = new ArrayList<>();

    /**
     * MulExp 乘除模表达式
     * 改写为 MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
     *
     * @throws Exception 异常
     */
    MulExp() throws Exception { //  MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp // 1.UnaryExp2.* 3./ 4.% 均需覆盖
        this.nt_type = NonTerminalType.MULEXP;
        ASTNode node2 = new ASTNode(new Token(new UnaryExp()));
        UnaryExp_list.add(node2);
        while (Parser.now.equalLexType(LexType.MULT)||Parser.now.equalLexType(LexType.DIV)||Parser.now.equalLexType(LexType.MOD)) {
            UnaryExp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            UnaryExp_list.add(new ASTNode(new Token(new UnaryExp())));
        }
        int i = UnaryExp_list.size();
        if (i == 1){ // UnaryExp
            UnaryExp.add(node2);
            setFirstchild(node2);
        } else { // MulExp ('*' | '/' | '%') UnaryExp
            UnaryExp.add(new ASTNode(new Token(new MulExp(UnaryExp_list))));
            UnaryExp.add(UnaryExp_list.get(i-2));
            UnaryExp.add(UnaryExp_list.get(i-1));
            setFirstchild(UnaryExp.get(0));
            UnaryExp.get(0).setNextSibling(UnaryExp.get(1));
            UnaryExp.get(1).setNextSibling(UnaryExp.get(2));
        }
    }

    /**
     * MulExp
     * 递归建造MulExp节点
     *
     * @param UnaryExp_list_prev 上一层MulExp结点的改写后文法序列
     * @throws Exception 异常
     */
    MulExp(ArrayList<ASTNode> UnaryExp_list_prev) throws Exception {
        this.nt_type = NonTerminalType.MULEXP;
        for (int i=0;i<UnaryExp_list_prev.size()-2;i++){
            UnaryExp_list.add(UnaryExp_list_prev.get(i));
        }
        int i = UnaryExp_list.size();
        if (i == 1) {
            UnaryExp.add(UnaryExp_list.get(0));
            setFirstchild(UnaryExp.get(0));
        } else {
            UnaryExp.add(new ASTNode(new Token(new MulExp(UnaryExp_list))));
            UnaryExp.add(UnaryExp_list.get(i-2));
            UnaryExp.add(UnaryExp_list.get(i-1));
            setFirstchild(UnaryExp.get(0));
            UnaryExp.get(0).setNextSibling(UnaryExp.get(1));
            UnaryExp.get(1).setNextSibling(UnaryExp.get(2));
        }
    }
}
