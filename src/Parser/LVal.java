package Parser;

import DataStructure.ASTNode;
import DataStructure.ErrorReporter;
import DataStructure.ErrorType;
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
    int ident_table = -1;
    ASTNode ident;
    int brackets_num = 0;
    LVal() throws Exception {
        this.nt_type = NonTerminalType.LVAL;
        if (Parser.now.isIdent()){
            int ident_line = Parser.now.line;
            if ((ident_table = Parser.cur.checkSymbol_use_string(Parser.now.token))==-1){
                ErrorReporter.reportError(ident_line, ErrorType.EC); // fixme:错误处理c
            }
            ident = new ASTNode(Parser.now);
            Exp_list.add(ident);
            Parser.lexer.next();
            for (int i=0;i<2;i++){
                if (!Parser.now.equalLexType(LexType.LBRACK))
                    break;
                Exp_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                Exp_list.add(new ASTNode(new Token(new Exp())));
                if (Parser.now.equalLexType(LexType.RBRACK))
                    Exp_list.add(new ASTNode(Parser.now));
                else {
//                    throw new CompilerException("2",Parser.now.line,"LVal");
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EK); // fixme:错误处理k
                    Exp_list.add(new ASTNode(new Token("]",LexType.RBRACK,Parser.prev.line))); // 补一个RBRACK，以保证可以正常按index取到dim的exp
                }
                Parser.lexer.next();
                brackets_num++;
            }
            setFirstchild(Exp_list.get(0));
            for (int i=0;i<Exp_list.size();i++){
                if (i+1<Exp_list.size()){
                    Exp_list.get(i).setNextSibling(Exp_list.get(i+1));
                }
            }
        }
    }

    public int getIdent_table(){
        return ident_table;
    }

    public String getIdentString() {
        return ((Token)ident.getData()).token;
    }

    public int getIdentLine() {
        return ((Token)ident.getData()).line;
    }

    public int getBrackets_num(){
        return brackets_num;
    }
}
