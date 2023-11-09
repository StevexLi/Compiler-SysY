package Parser;

import DataStructure.ASTNode;
import DataStructure.ErrorReporter;
import DataStructure.ErrorType;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

import java.util.ArrayList;

public class ConstDecl extends NonTerminal {
    ASTNode _const_;
    ASTNode BType;
    ArrayList<ASTNode> ConstDef = new ArrayList<>();
    ASTNode SEMICN;
    ConstDecl() throws Exception { // 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // 1.花括号内重复0次 2.花括号内重复多次
        this.nt_type = NonTerminalType.CONSTDECL;
        if (Parser.now.equalLexType(LexType.CONSTTK)){
            _const_ = new ASTNode(Parser.now);
            if (Parser.lexer.preRead().equalLexType(LexType.INTTK)){
                Parser.lexer.next();
                BType = new ASTNode(new Token(new BType()));
                ConstDef.add(new ASTNode(new Token(new ConstDef(LexType.INTTK))));
                while (Parser.now.equalLexType(LexType.COMMA)) {
                    ConstDef.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    ConstDef.add(new ASTNode(new Token(new ConstDef(LexType.INTTK))));
                }
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    SEMICN = new ASTNode(Parser.now);
                    Parser.lexer.next();
                } else {
//                    throw new CompilerException("2",Parser.now.line,"ConstDecl2");
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                }
            }
        } else {
            throw new CompilerException("2",Parser.now.line,"ConstDecl");
        }
        setFirstchild(_const_);
        _const_.setNextSibling(BType);
        ASTNode node1 = new ASTNode();
        ASTNode node0 = BType;
        for (int i=0;i<ConstDef.size();i++){
            node1 = ConstDef.get(i);
            if (i==0) {
                node0.setNextSibling(node1);
            }
            if (i<ConstDef.size()-1)
                node1.setNextSibling(ConstDef.get(i+1));
            node0 = node1;
        }
        node0.setNextSibling(SEMICN);
    }
}
