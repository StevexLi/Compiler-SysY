package Parser;

import Lexer.LexType;
import Lexer.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 变量定义 VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义
 * | Ident { '[' ConstExp ']' } '=' InitVal
 * @author Stevex
 * @date 2023/10/13
 */
public class VarDef extends NonTerminal {
    ASTNode Ident;
    ArrayList<ASTNode> ConstExp = new ArrayList<>();
    ASTNode ASSIGN;
    ASTNode InitVal;
    VarDef() throws Exception {
        this.nt_type = NonTerminalType.VARDEF;
        if (Parser.now.isIdent()) {
            Parser.now.is_const = false; // ！
            Ident = new ASTNode(Parser.now);
            Parser.lexer.next();
            for (int i=0;i<2;i++) { // 普通变量、一维数组、二维数组
                if (!Parser.now.equalLexType(LexType.LBRACK))
                    break;
                if (Parser.now.equalLexType(LexType.LBRACK)){
                    ConstExp.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    ConstExp.add(new ASTNode(new Token(new ConstExp())));
                    if (Parser.now.equalLexType(LexType.RBRACK)){
                        ConstExp.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
                        throw new CompilerException("2",Parser.now.line,"VarDef3");
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line,"VarDef2");
                }
            }
            setFirstchild(Ident);
            ASTNode node1 = new ASTNode();
            ASTNode node0 = Ident;
            for (int i=0;i<ConstExp.size();i++){
                node1 = ConstExp.get(i);
                if (i==0) {
                    node0.setNextSibling(node1);
                }
                if (i<ConstExp.size()-1)
                    node1.setNextSibling(ConstExp.get(i+1));
                node0 = node1;
            }
            if (Parser.now.equalLexType(LexType.ASSIGN)) { // Ident { '[' ConstExp ']' } '=' InitVal
                ASSIGN = new ASTNode(Parser.now);
                Parser.lexer.next();
                InitVal = new ASTNode(new Token(new InitVal()));
                node0.setNextSibling(ASSIGN);
                ASSIGN.setNextSibling(InitVal);
            }
        } else {
            throw new CompilerException("2",Parser.now.line,"VarDef");
        }
    }
}
