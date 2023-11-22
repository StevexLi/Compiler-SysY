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
    boolean in_for = false;
    BlockItem(boolean in_for_loop) throws Exception {
        this.in_for = in_for_loop;
        this.nt_type = NonTerminalType.BLOCKITEM;
        if (Parser.now.equalLexType(LexType.INTTK)||Parser.now.equalLexType(LexType.CONSTTK)){ // Decl
            Decl = new ASTNode(new Token(new Decl()));
            setFirstchild(Decl);
        } else { // Stmt
            Stmt = new ASTNode(new Token(new Stmt(in_for)));
            setFirstchild(Stmt);
        }
    }

    public Decl getDecl() {
        if (Decl==null)
            return null;
        return ((Decl)(this.Decl.getDataToken().nt));
    }

    public Stmt getStmt() {
        if (Stmt==null)
            return null;
        return ((Stmt)(this.Stmt.getDataToken().nt));
    }
}
