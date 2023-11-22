package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number // 三种情况均需覆盖
 *
 * @author Stevex
 * @date 2023/10/12
 */
public class PrimaryExp extends NonTerminal {
    ArrayList<ASTNode> Exp = new ArrayList<>();
    ASTNode LVal;
    ASTNode Number;
    int primary_exp_type = -1;
    PrimaryExp() throws Exception{
        this.nt_type = NonTerminalType.PRIMARYEXP;
        if (Parser.now.equalLexType(LexType.LPARENT)) { // (Exp)
            primary_exp_type = 1;
            Exp.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            Exp.add(new ASTNode(new Token(new Exp())));
            if (Parser.now.equalLexType(LexType.RPARENT)){
                Exp.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                setFirstchild(Exp.get(0));
                Exp.get(0).setNextSibling(Exp.get(1));
                Exp.get(1).setNextSibling(Exp.get(2));
            } else {
                throw new CompilerException("2",Parser.now.line,"PrimaryExp1");
            }
        } else if (Parser.now.isIdent()){
            primary_exp_type = 2;
            LVal = new ASTNode(new Token(new LVal()));
            setFirstchild(LVal);
        } else {
            if (Parser.now.isIntConst()){
                primary_exp_type = 3;
                Number = new ASTNode(new Token(new _Number_()));
                setFirstchild(Number);
            } else {
//                throw new CompilerException("2",Parser.now.line,"PrimaryExp2");
            }
        }
    }
    public int getPrimary_exp_type(){
        return primary_exp_type;
    }

    public _Number_ getNumber() {
        if (Number==null)
            return null;
        return (_Number_) (this.Number.getDataToken().nt);
    }

    public Exp getExp() {
        if (Exp.isEmpty())
            return null;
        return (Exp) Exp.get(1).getDataToken().nt;
    }

    public LVal getLVal() {
        if (LVal==null)
            return null;
        return (LVal) LVal.getDataToken().nt;
    }
}
