package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import Exception.*;
import Lexer.Token;

import java.util.ArrayList;

/**
 * 常数定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal // 包含普通变量、一维
 * 数组、二维数组共三种情况
 * @author Stevex
 * @date 2023/10/13
 */
public class ConstDef extends NonTerminal {
    ASTNode Ident;
    ArrayList<ASTNode> ConstExp = new ArrayList<>();
    ASTNode ASSIGN;
    ASTNode ConstInitVal;
    ConstDef() throws Exception {
        this.nt_type = NonTerminalType.CONSTDEF;
        if (Parser.now.isIdent()) {
            Parser.now.is_const = true;
            Ident = new ASTNode(Parser.now);
            Parser.lexer.next();
            for (int i=0;i<2;i++) { // 普通变量、一维数组、二维数组
                if (Parser.now.equalLexType(LexType.ASSIGN))
                    break;
                if (Parser.now.equalLexType(LexType.LBRACK)){
                    ConstExp.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    ConstExp.add(new ASTNode(new Token(new ConstExp())));
                    if (Parser.now.equalLexType(LexType.RBRACK)){
                        ConstExp.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
                        throw new CompilerException("2",Parser.now.line,"ConstDef3");
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line,"ConstDef2");
                }
            }
            if (Parser.now.equalLexType(LexType.ASSIGN)) {
                ASSIGN = new ASTNode(Parser.now);
            } else {
                throw new CompilerException("2",Parser.now.line,"ConstDef1");
            }
            Parser.lexer.next();
            ConstInitVal = new ASTNode(new Token(new ConstInitVal()));
        } else {
            throw new CompilerException("2",Parser.now.line,"ConstDef");
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
        node0.setNextSibling(ASSIGN);
        ASSIGN.setNextSibling(ConstInitVal);
    }
}
