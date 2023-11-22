package Parser;

import DataStructure.*;
import Lexer.LexType;

import java.util.ArrayList;

/**
 * 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
 *
 * @author Stevex
 * @date 2023/10/13
 */
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
        } else {
            ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
            RPARENT = new ASTNode(new Token(")",LexType.RPARENT,Parser.prev.line)); // 补一个')'，保证语法树结构
        }
        Block = new ASTNode(new Token(new Block(SymbolType.MAINFUNC,null, new ArrayList<>())));
        setFirstchild(_int_);
        _int_.setNextSibling(main);
        main.setNextSibling(LPARENT);
        LPARENT.setNextSibling(RPARENT);
        RPARENT.setNextSibling(Block);
    }

    public Block getBlock() {
        return ((Block)(this.Block.getDataToken().nt));
    }
}
