package Parser;

import DataStructure.ASTNode;
import DataStructure.Token;
import Lexer.LexType;
import Exception.*;

public class BType extends NonTerminal {
    ASTNode _int_;
    BType() throws Exception {
        this.nt_type = NonTerminalType.BTYPE;
        if (Parser.now.equalLexType(LexType.INTTK)){
            _int_ = new ASTNode(Parser.now);
            setFirstchild(_int_);
        } else {
            throw new CompilerException("2",Parser.now.line,"Btype");
        }
        Parser.lexer.next();
    }

    LexType getType() {
        return ((Token)(_int_.getData())).type;
    }
}
