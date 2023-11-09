package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;

public class FuncType extends NonTerminal {
    ASTNode FuncType;
    LexType type;
    FuncType() throws Exception {
        this.nt_type = NonTerminalType.FUNCTYPE;
        if (Parser.now.equalLexType(LexType.INTTK)||Parser.now.equalLexType(LexType.VOIDTK)){
            type = Parser.now.type;
            FuncType = new ASTNode(Parser.now);
            Parser.lexer.next();
            setFirstchild(FuncType);
        }
    }
}
