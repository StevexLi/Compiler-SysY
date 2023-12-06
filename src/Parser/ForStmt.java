package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

/**
 * 语句 ForStmt → LVal '=' Exp // 存在即可
 * @author Stevex
 * @date 2023/10/14
 */
public class ForStmt extends NonTerminal {
    ASTNode LVal;
    ASTNode ASSIGN;
    ASTNode Exp;
    ForStmt() throws Exception {
        this.nt_type = NonTerminalType.FORSTMT;
        LVal = new ASTNode(new Token(new LVal()));
        if (Parser.now.equalLexType(LexType.ASSIGN)){
            ASSIGN = new ASTNode(Parser.now);
            Parser.lexer.next();
            Exp = new ASTNode(new Token(new Exp()));
            setFirstchild(LVal);
            LVal.setNextSibling(ASSIGN);
            ASSIGN.setNextSibling(Exp);
        }
    }

    public LVal getLVal() {
        if (LVal==null)
            return null;
        return (LVal) LVal.getDataToken().nt;
    }

    public Exp getExp_single() {
        if (Exp==null)
            return null;
        return (Exp) Exp.getDataToken().nt;
    }
}
