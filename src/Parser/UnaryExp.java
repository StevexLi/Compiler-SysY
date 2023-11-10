package Parser;

import DataStructure.ASTNode;
import DataStructure.ErrorReporter;
import DataStructure.ErrorType;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * UnaryExp 一元表达式
 * UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
 *
 * @author Stevex
 * @date 2023/10/12
 */
public class UnaryExp extends NonTerminal { //
    ASTNode PrimaryExp;
    ArrayList<ASTNode> FuncRParams = new ArrayList<>();
    ASTNode UnaryOp;
    ASTNode UnaryExp;
    UnaryExp() throws Exception{
        this.nt_type = NonTerminalType.UNARYEXP;
        if (Parser.now.equalLexType(LexType.PLUS)||Parser.now.equalLexType(LexType.MINU)||Parser.now.equalLexType(LexType.NOT)){ // UnaryOp UnaryExp
            UnaryOp = new ASTNode(new Token(new UnaryOp()));
            UnaryExp = new ASTNode(new Token(new UnaryExp()));
            setFirstchild(UnaryOp);
            UnaryOp.setNextSibling(UnaryExp);
        } else if (Parser.now.equalLexType(LexType.IDENFR) && Parser.lexer.preRead().equalLexType(LexType.LPARENT)){ //  Ident '(' [FuncRParams] ')'
            int ident_line = Parser.now.line;
            int ident_table;
            if ((ident_table = Parser.cur.checkSymbol_use_string(Parser.now.token))==-1){
                ErrorReporter.reportError(ident_line, ErrorType.EC); // fixme:错误处理c
            }
            FuncRParams.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            FuncRParams.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            if (!Parser.now.equalLexType(LexType.RPARENT)) {
                FuncRParams.add(new ASTNode(new Token(new FuncRParams())));
                if (Parser.now.equalLexType(LexType.RPARENT)){
                    FuncRParams.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
//                    throw new CompilerException("2",Parser.now.line,"UnaryExp");
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
                }
            } else {
                FuncRParams.add(new ASTNode(Parser.now));
                Parser.lexer.next();
            }
            setFirstchild(FuncRParams.get(0));
            for (int i=0;i<FuncRParams.size();i++){
                if (i+1<FuncRParams.size()){
                    FuncRParams.get(i).setNextSibling(FuncRParams.get(i+1));
                }
            }
        } else { // PrimaryExp
            PrimaryExp = new ASTNode(new Token(new PrimaryExp()));
            setFirstchild(PrimaryExp);
        }
    }
}
