package Parser;

import DataStructure.ASTNode;

public class _Number_ extends NonTerminal {
    ASTNode _Number_;
    int value;
    _Number_() throws Exception {
        this.nt_type = NonTerminalType.NUMBER;
        if (Parser.now.isIntConst()){
            _Number_ = new ASTNode(Parser.now);
            value = Parser.now.value;
            setFirstchild(_Number_);
            Parser.lexer.next();
        }
    }
}
