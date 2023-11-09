package Parser;

import DataStructure.*;
import Lexer.LexType;
import Exception.*;

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
    ConstDef(LexType value_type) throws Exception {
        this.nt_type = NonTerminalType.CONSTDEF;
        if (Parser.now.isIdent()) {
            Parser.now.is_const = true;
            Ident = new ASTNode(Parser.now);
            int ident_line = Parser.now.line;
            if (Parser.cur.checkSymbol_def_string(Parser.now.token)){
                ErrorReporter.reportError(ident_line, ErrorType.EB); // fixme:错误处理b
            }
            Parser.lexer.next();
            int dim = 0;
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
//                        throw new CompilerException("2",Parser.now.line,"ConstDef3");
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EK); // fixme:错误处理k
                        ConstExp.add(new ASTNode(new Token("]",LexType.RBRACK,Parser.prev.line))); // 补一个RBRACK，以保证可以正常按index取到dim的exp
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line,"ConstDef2");
                }
                dim++;
            }
            if (Parser.now.equalLexType(LexType.ASSIGN)) {
                ASSIGN = new ASTNode(Parser.now);
            } else {
                throw new CompilerException("2",Parser.now.line,"ConstDef1");
            }
            Parser.lexer.next();
            ConstInitVal = new ASTNode(new Token(new ConstInitVal()));
            switch (dim) {
                case 0: // 普通变量
                    Parser.cur.addSymbol(new Symbol(Ident, SymbolType.VAR, true, value_type, true, null, null, ConstInitVal));
                    break;
                case 1: // 一维数组
                    Parser.cur.addSymbol(new Symbol(Ident, SymbolType.DIM1ARRAY, true, value_type, true, ConstExp.get(1), null, ConstInitVal));
                    break;
                case 2: // 二维数组
                    Parser.cur.addSymbol(new Symbol(Ident, SymbolType.DIM2ARRAY, true, value_type, true, ConstExp.get(1), ConstExp.get(4), ConstInitVal));
                    break;
            }
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
