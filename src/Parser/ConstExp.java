package Parser;

import Lexer.Token;

public class ConstExp extends NonTerminal {
    ASTNode AddExp;
    ConstExp() throws Exception {
        this.nt_type = NonTerminalType.CONSTEXP;
        AddExp = new ASTNode(new Token(new AddExp()));
        setFirstchild(AddExp);
    }
}
