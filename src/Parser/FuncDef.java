package Parser;

import DataStructure.ASTNode;
import DataStructure.ErrorReporter;
import DataStructure.ErrorType;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

/**
 * 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // 1.无形参 2.有形参
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class FuncDef extends NonTerminal {
    ASTNode FuncType;
    ASTNode Ident;
    ArrayList<ASTNode> FuncFParams = new ArrayList<>();
    ASTNode Block;
    FuncDef() throws Exception{
        this.nt_type = NonTerminalType.FUNCDEF;
        FuncType = new ASTNode(new Token(new FuncType()));
        if (Parser.now.isIdent()){
            Ident = new ASTNode(Parser.now);
            Parser.lexer.next();
            if (Parser.now.equalLexType(LexType.LPARENT)) {
                FuncFParams.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (!Parser.now.equalLexType(LexType.RPARENT)){
                    FuncFParams.add(new ASTNode(new Token(new FuncFParams())));
                }
                if (Parser.now.equalLexType(LexType.RPARENT)){
                    FuncFParams.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
                }
                Block = new ASTNode(new Token(new Block()));
                setFirstchild(FuncType);
                FuncType.setNextSibling(Ident);
                ASTNode node1 = new ASTNode();
                ASTNode node0 = Ident;
                for (int i=0;i<FuncFParams.size();i++){
                    node1 = FuncFParams.get(i);
                    if (i==0){
                        node0.setNextSibling(node1);
                    }
                    if (i<FuncFParams.size()-1)
                        node1.setNextSibling(FuncFParams.get(i+1));
                    node0 = node1;
                }
                node1.setNextSibling(Block);
            }
        }
    }
}
