package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 左值表达式 LVal → Ident {'[' Exp ']'} //1.普通变量 2.一维数组 3.二维数组
 *
 * @author Stevex
 * @date 2023/10/12
 */
public class LVal extends NonTerminal {
    ArrayList<ASTNode> Exp_list = new ArrayList<>();
    LVal() throws Exception {
        this.nt_type = NonTerminalType.LVAL;
        if (Parser.now.isIdent()){
            Exp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            for (int i=0;i<2;i++){
                if (!Parser.now.equalLexType(LexType.LBRACK))
                    break;
                Exp_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                Exp_list.add(new ASTNode(new Token(new Exp())));
                if (Parser.now.equalLexType(LexType.RBRACK))
                    Exp_list.add(new ASTNode(Parser.now));
                else
                    throw new CompilerException("2",Parser.now.line,"LVal");
                Parser.lexer.next();
            }
            setFirstchild(Exp_list.get(0));
            for (int i=0;i<Exp_list.size();i++){
                if (i+1<Exp_list.size()){
                    Exp_list.get(i).setNextSibling(Exp_list.get(i+1));
                }
            }
        }
    }
}
