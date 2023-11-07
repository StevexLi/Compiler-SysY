package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import Lexer.Token;

import java.util.ArrayList;

public class AddExp extends NonTerminal {
    ArrayList<ASTNode> MulExp_list = new ArrayList<>();
    ArrayList<ASTNode> MulExp = new ArrayList<>();

    /**
     * AddExp 加减表达式
     * 改写为 AddExp → MulExp {('+' | '−') MulExp}
     *
     * @throws Exception
     */
    AddExp() throws Exception { // AddExp → MulExp | AddExp ('+' | '−') MulExp // 1.MulExp 2.+ 需覆盖 3.- 需覆盖
        this.nt_type = NonTerminalType.ADDEXP;
        ASTNode node2 = new ASTNode(new Token(new MulExp()));
        MulExp_list.add(node2);
        while (Parser.now.equalLexType(LexType.PLUS)||Parser.now.equalLexType(LexType.MINU)) {
            MulExp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            MulExp_list.add(new ASTNode(new Token(new MulExp())));
        }
        int i = MulExp_list.size();
        if (i == 1){ // MulExp
            MulExp.add(node2);
            setFirstchild(node2);
        } else { // AddExp ('+' | '−') MulExp
            MulExp.add(new ASTNode(new Token(new AddExp(MulExp_list))));
            MulExp.add(MulExp_list.get(i-2));
            MulExp.add(MulExp_list.get(i-1));
            setFirstchild(MulExp.get(0));
            MulExp.get(0).setNextSibling(MulExp.get(1));
            MulExp.get(1).setNextSibling(MulExp.get(2));
        }
    }

    /**
     * 递归建造AddExp节点
     *
     * @param MulExp_list_prev 上一层AddExp结点的改写后文法序列
     * @throws Exception
     */
    AddExp(ArrayList<ASTNode> MulExp_list_prev) throws Exception {
        this.nt_type = NonTerminalType.ADDEXP;
        for (int i=0;i<MulExp_list_prev.size()-2;i++){
            MulExp_list.add(MulExp_list_prev.get(i));
        }
        int i = MulExp_list.size();
        if (i == 1) {
            MulExp.add(MulExp_list.get(0));
            setFirstchild(MulExp.get(0));
        } else {
            MulExp.add(new ASTNode(new Token(new AddExp(MulExp_list))));
            MulExp.add(MulExp_list.get(i-2));
            MulExp.add(MulExp_list.get(i-1));
            setFirstchild(MulExp.get(0));
            MulExp.get(0).setNextSibling(MulExp.get(1));
            MulExp.get(1).setNextSibling(MulExp.get(2));
        }
    }
}
