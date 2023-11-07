package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import Lexer.Token;

public class MainFuncDef extends NonTerminal {
    ASTNode _int_;
    ASTNode main;
    ASTNode LPARENT;
    ASTNode RPARENT;
    ASTNode Block;
    MainFuncDef() throws Exception {
        this.nt_type = NonTerminalType.MAINFUNCDEF;
        if (Parser.now.equalLexType(LexType.INTTK)){
            _int_ = new ASTNode(Parser.now);
            Parser.lexer.next();
        }
        if (Parser.now.equalLexType(LexType.MAINTK)){
            main = new ASTNode(Parser.now);
            Parser.lexer.next();
        }
        if (Parser.now.equalLexType(LexType.LPARENT)){
            LPARENT = new ASTNode(Parser.now);
            Parser.lexer.next();
        }
        if (Parser.now.equalLexType(LexType.RPARENT)){
            RPARENT = new ASTNode(Parser.now);
            Parser.lexer.next();
        }
        Block = new ASTNode(new Token(new Block()));
        setFirstchild(_int_);
        _int_.setNextSibling(main);
        main.setNextSibling(LPARENT);
        LPARENT.setNextSibling(RPARENT);
        RPARENT.setNextSibling(Block);
    }
}
