package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

/**
 * 语句块项 BlockItem → Decl | Stmt // 覆盖两种语句块项
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class BlockItem extends NonTerminal {
    ASTNode Decl;
    ASTNode Stmt;
    BlockItem() throws Exception {
        this.nt_type = NonTerminalType.BLOCKITEM;
        if (Parser.now.equalLexType(LexType.INTTK)||Parser.now.equalLexType(LexType.CONSTTK)){ // Decl
            Decl = new ASTNode(new Token(new Decl()));
            setFirstchild(Decl);
        } else { // Stmt
            Stmt = new ASTNode(new Token(new Stmt()));
            setFirstchild(Stmt);
        }
    }
}
