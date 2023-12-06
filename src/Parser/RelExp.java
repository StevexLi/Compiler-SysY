package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

/**
 * 关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp // 1.AddExp
 * 2.< 3.> 4.<= 5.>= 均需覆盖
 * @author Stevex
 * @date 2023/10/14
 */
public class RelExp extends NonTerminal {
    ArrayList<ASTNode> AddExp_list = new ArrayList<>();
    ArrayList<ASTNode> AddExp = new ArrayList<>();

    /**
     * RelExp 关系表达式
     * 改写为 RelExp → AddExp {  ('<' | '>' | '<=' | '>=') AddExp}
     *
     * @throws Exception 异常
     */
    RelExp() throws Exception {
        this.nt_type = NonTerminalType.RELEXP;
        ASTNode node2 = new ASTNode(new Token(new AddExp()));
        AddExp_list.add(node2);
        while (Parser.now.equalLexType(LexType.LSS)||Parser.now.equalLexType(LexType.GRE)||Parser.now.equalLexType(LexType.LEQ)||Parser.now.equalLexType(LexType.GEQ)) {
            AddExp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            AddExp_list.add(new ASTNode(new Token(new AddExp())));
        }
        int i = AddExp_list.size();
        if (i == 1){ // AddExp
            AddExp.add(node2);
            setFirstchild(node2);
        } else { // RelExp ('<' | '>' | '<=' | '>=') AddExp
            AddExp.add(new ASTNode(new Token(new RelExp(AddExp_list))));
            AddExp.add(AddExp_list.get(i-2));
            AddExp.add(AddExp_list.get(i-1));
            setFirstchild(AddExp.get(0));
            AddExp.get(0).setNextSibling(AddExp.get(1));
            AddExp.get(1).setNextSibling(AddExp.get(2));
        }
    }

    /**
     * RelExp
     * 递归建造RelExp节点
     *
     * @param AddExp_list_prev 上一层RelExp结点的改写后文法序列
     * @throws Exception 异常
     */
    RelExp(ArrayList<ASTNode> AddExp_list_prev) throws Exception {
        this.nt_type = NonTerminalType.RELEXP;
        for (int i=0;i<AddExp_list_prev.size()-2;i++){
            AddExp_list.add(AddExp_list_prev.get(i));
        }
        int i = AddExp_list.size();
        if (i == 1) {
            AddExp.add(AddExp_list.get(0));
            setFirstchild(AddExp.get(0));
        } else {
            AddExp.add(new ASTNode(new Token(new RelExp(AddExp_list))));
            AddExp.add(AddExp_list.get(i-2));
            AddExp.add(AddExp_list.get(i-1));
            setFirstchild(AddExp.get(0));
            AddExp.get(0).setNextSibling(AddExp.get(1));
            AddExp.get(1).setNextSibling(AddExp.get(2));
        }
    }

    public ArrayList<Token> getAddExp_list() {
        ArrayList<Token> list = new ArrayList<>();
        for (ASTNode node : AddExp_list){
            list.add(node.getDataToken());
        }
        return list;
    }
}
