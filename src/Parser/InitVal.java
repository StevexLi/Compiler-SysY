package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'// 1.表达式初值 2.一维数
 * 组初值 3.二维数组初值
 * @author Stevex
 * @date 2023/10/13
 */
public class InitVal extends NonTerminal {
    ASTNode Exp;
    ArrayList<ASTNode> InitVal_list = new ArrayList<>();
    InitVal() throws Exception {
        this.nt_type = NonTerminalType.INITVAL;
        if (!Parser.now.equalLexType(LexType.LBRACE)){ // InitVal → Exp
            Exp = new ASTNode(new Token(new Exp()));
            setFirstchild(Exp);
        } else {
            InitVal_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            if (Parser.now.equalLexType(LexType.RBRACE)){ // {}
                InitVal_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
            } else { //   [ InitVal { ',' InitVal } ]
                InitVal_list.add(new ASTNode(new Token(new InitVal())));
                while (Parser.now.equalLexType(LexType.COMMA)){
                    InitVal_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    InitVal_list.add(new ASTNode(new Token(new InitVal())));
                }
                if (Parser.now.equalLexType(LexType.RBRACE)){
                    InitVal_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    throw new CompilerException("2",Parser.now.line,"InitVal");
                }
            }
            setFirstchild(InitVal_list.get(0));
            for (int i = 0; i< InitVal_list.size(); i++){
                if (i+1< InitVal_list.size()){
                    InitVal_list.get(i).setNextSibling(InitVal_list.get(i+1));
                }
            }
        }
    }

    public Exp getExp() {
        if (Exp==null)
            return null;
        return (Exp) Exp.getDataToken().nt;
    }
}
