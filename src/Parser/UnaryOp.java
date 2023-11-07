package Parser;

import Lexer.LexType;
import Exception.*;

public class UnaryOp extends NonTerminal {
    ASTNode UnaryOp;
    UnaryOp() throws Exception {
        this.nt_type = NonTerminalType.UNARYOP;
        if (Parser.now.equalLexType(LexType.PLUS)||Parser.now.equalLexType(LexType.MINU)||Parser.now.equalLexType(LexType.NOT)){
            UnaryOp = new ASTNode(Parser.now);
            setFirstchild(UnaryOp);
            Parser.lexer.next();
        } else {
            throw new CompilerException("2",Parser.now.line,"UnaryOp");
        }
    }
}
