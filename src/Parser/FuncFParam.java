package Parser;

import DataStructure.ASTNode;
import DataStructure.ErrorReporter;
import DataStructure.ErrorType;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

/**
 * 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam } // 1.花括号内重复0次 2.花括号内
 * 重复多次
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class FuncFParam extends NonTerminal {
    ASTNode BType;
    ASTNode Ident;
    ArrayList<ASTNode> Params_list = new ArrayList<>();
    int dim = 0;
    ASTNode const_exp;
    FuncFParam() throws Exception {
        this.nt_type = NonTerminalType.FUNCFPARAM;
        BType = new ASTNode(new Token(new BType()));
        if (Parser.now.isIdent()){
            Ident = new ASTNode(Parser.now); // 普通变量
//            int ident_line = Parser.now.line;
//            if (Parser.cur.checkSymbol_def_string(Parser.now.token)){
//                ErrorReporter.reportError(ident_line, ErrorType.EB); // fixme:错误处理b
//            }
            Parser.lexer.next();
            if (Parser.now.equalLexType(LexType.LBRACK)){
                Params_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                dim++;
                if (Parser.now.equalLexType(LexType.RBRACK)){
                    Params_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EK); // fixme:错误处理k
                    Params_list.add(new ASTNode(new Token("]",LexType.RBRACK,Parser.prev.line))); // 补一个RBRACK，以保证可以正常按index取到dim的exp
                }
                // 一维数组
                if (Parser.now.equalLexType(LexType.LBRACK)){ // 二维数组
                    Params_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    dim++;
                    const_exp = new ASTNode(new Token(new ConstExp()));
                    Params_list.add(const_exp);
                    if (Parser.now.equalLexType(LexType.RBRACK)){
                        Params_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EK); // fixme:错误处理k
                        Params_list.add(new ASTNode(new Token("]",LexType.RBRACK,Parser.prev.line))); // 补一个RBRACK，以保证可以正常按index取到dim的exp
                    }
                }
            }
            setFirstchild(BType);
            BType.setNextSibling(Ident);
            for (int i=0;i<Params_list.size();i++){
                if (i==0)
                    Ident.setNextSibling(Params_list.get(0));
                if (i+1<Params_list.size()){
                    Params_list.get(i).setNextSibling(Params_list.get(i+1));
                }
            }
        }
    }

    ASTNode getIdent() {
        return Ident;
    }

    ASTNode getConst_exp() {
        return const_exp;
    }

    LexType getType() {
        return ((BType)((Token)BType.getData()).nt).getType();
    }

    int getDim() {
        return dim;
    }

}
