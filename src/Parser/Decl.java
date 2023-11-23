package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

public class Decl extends NonTerminal {
    ASTNode ConstDecl;
    ASTNode VarDecl;
    Decl() throws Exception { // 声明 Decl → ConstDecl | VarDecl // 覆盖两种声明
        this.nt_type = NonTerminalType.DECL;
        if (Parser.now.equalLexType(LexType.CONSTTK)) { // ConstDecl
            ConstDecl = new ASTNode(new Token(new ConstDecl()));
        } else {
            VarDecl = new ASTNode(new Token(new VarDecl()));
        }
        setFirstchild(ConstDecl);
        setFirstchild(VarDecl);
    }

    public ConstDecl getConstDecl() {
        if (ConstDecl==null)
            return null;
        return (ConstDecl) ConstDecl.getDataToken().nt;
    }

    public VarDecl getVarDecl() {
        if (VarDecl==null)
            return null;
        return (VarDecl) VarDecl.getDataToken().nt;
    }
}
