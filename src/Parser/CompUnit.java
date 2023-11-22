package Parser;

import DataStructure.ASTNode;
import DataStructure.SymbolTable;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef // 1.是否存在Decl 2.是否存在FuncDef
 *
 * @author Stevex
 * @date 2023/10/14
 */
public class CompUnit extends NonTerminal {
    ArrayList<ASTNode> Decl = new ArrayList<>();
    ArrayList<ASTNode> FuncDef = new ArrayList<>();
    ASTNode MainFuncDef;
    MainFuncDef mfd;
    CompUnit() throws Exception {
        this.nt_type = NonTerminalType.COMPUNIT;
        Parser.root = Parser.cur = new SymbolTable(Parser.s_table_list.size(),-1);
        Parser.s_table_list.add(Parser.cur);
        while (Parser.now.equalLexType(LexType.INTTK) || Parser.now.equalLexType(LexType.CONSTTK) || Parser.now.equalLexType(LexType.VOIDTK)) {
            if (Parser.now.equalLexType(LexType.INTTK)) {
                if (Parser.lexer.preRead().equalLexType(LexType.MAINTK)) {
                    mfd = new MainFuncDef();
                    MainFuncDef = new ASTNode(new Token(mfd));
                } else if (Parser.lexer.preRead().isIdent()) {
                    if (Parser.lexer.prepreRead().equalLexType(LexType.ASSIGN)||Parser.lexer.prepreRead().equalLexType(LexType.LBRACK)||Parser.lexer.prepreRead().equalLexType(LexType.SEMICN)||Parser.lexer.prepreRead().equalLexType(LexType.COMMA)) {
                        Decl.add(new ASTNode(new Token(new Decl())));
                    } else if (Parser.lexer.prepreRead().equalLexType(LexType.LPARENT)) {
                        FuncDef.add(new ASTNode(new Token(new FuncDef())));
                    } else {
                        throw new CompilerException("2",Parser.now.line,"CompUnit1");
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line,"CompUnit2");
                }
            } else if (Parser.now.equalLexType(LexType.CONSTTK)) { // Decl->ConstDecl
                Decl.add(new ASTNode(new Token(new Decl())));
            } else if (Parser.now.equalLexType(LexType.VOIDTK)) { // FuncDef → void Ident '(' [FuncFParams] ')' Block
                FuncDef.add(new ASTNode(new Token(new FuncDef())));
            } else {
                throw new CompilerException("2",Parser.now.line,"CompUnit3");
            }
        }
//        while (Parser.now!=null) {
//            System.out.println(Parser.now);
//                    MainFuncDef = new ASTNode(new Token(new MainFuncDef()));
//                    Decl.add(new ASTNode(new Token(new Decl())));
//                    FuncDef.add(new ASTNode(new Token(new FuncDef())));
//                    Decl.add(new ASTNode(new Token(new Decl())));
//                    Parser.lexer.next();
//        }
//        if (MainFuncDef!=null) { // 建树:别忘了！！！
            ASTNode node1 = new ASTNode();
            ASTNode node0 = new ASTNode();
            for (int i=0;i<Decl.size();i++){
                node1 = Decl.get(i);
                setFirstchild(node1);
                if (i<Decl.size()-1)
                    node1.setNextSibling(Decl.get(i+1));
                node0 = node1;
            }
            for (int i=0;i<FuncDef.size();i++){
                node1 = FuncDef.get(i);
                if (i==0) {
                    node0.setNextSibling(node1);
                }
                setFirstchild(node1);
                if (i<FuncDef.size()-1)
                    node1.setNextSibling(FuncDef.get(i+1));
                node0 = node1;
            }
            setFirstchild(MainFuncDef);
            node0.setNextSibling(MainFuncDef);
//        }
    }

    public MainFuncDef getMainFuncDef() {
        return mfd;
    }
}
