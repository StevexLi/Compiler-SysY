package Parser;

import DataStructure.*;
import Lexer.LexType;
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
    int dim = 0;
    ArrayList<ConstExp> const_exp_list = new ArrayList<>();
    VarDef(LexType value_type) throws Exception {
        this.nt_type = NonTerminalType.VARDEF;
        if (Parser.now.isIdent()) {
            Parser.now.is_const = false; // ！
            Ident = new ASTNode(Parser.now);
            int ident_line = Parser.now.line;
            if (Parser.cur.checkSymbol_def_string(Parser.now.token)){
                ErrorReporter.reportError(ident_line, ErrorType.EB); // fixme:错误处理b
            }
            Parser.lexer.next();
            dim = 0;
            for (int i=0;i<2;i++) { // 普通变量、一维数组、二维数组
                if (!Parser.now.equalLexType(LexType.LBRACK))
                    break;
                if (Parser.now.equalLexType(LexType.LBRACK)){
                    ConstExp.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    ConstExp e = new ConstExp();
                    const_exp_list.add(e);
                    ConstExp.add(new ASTNode(new Token(e)));
                    if (Parser.now.equalLexType(LexType.RBRACK)){
                        ConstExp.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
//                        throw new CompilerException("2",Parser.now.line,"VarDef3");
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EK); // fixme:错误处理k
                        ConstExp.add(new ASTNode(new Token("]",LexType.RBRACK,Parser.prev.line))); // 补一个RBRACK，以保证可以正常按index取到dim的exp
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line,"VarDef2");
                }
                dim++;
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
            boolean has_value = false;
            if (Parser.now.equalLexType(LexType.ASSIGN)) { // Ident { '[' ConstExp ']' } '=' InitVal
                ASSIGN = new ASTNode(Parser.now);
                Parser.lexer.next();
                InitVal = new ASTNode(new Token(new InitVal()));
                node0.setNextSibling(ASSIGN);
                ASSIGN.setNextSibling(InitVal);
                has_value = true;
            }
            switch (dim){
                case 0: // 普通变量
                    Parser.cur.addSymbol(new Symbol(Ident,SymbolType.VAR,false,value_type,has_value,null,null,InitVal));
                    break;
                case 1: // 一维数组
                    Parser.cur.addSymbol(new Symbol(Ident,SymbolType.DIM1ARRAY,false,value_type,has_value,ConstExp.get(1),null,InitVal));
                    break;
                case 2: // 二维数组
                    Parser.cur.addSymbol(new Symbol(Ident,SymbolType.DIM2ARRAY,false,value_type,has_value,ConstExp.get(1),ConstExp.get(4),InitVal));
                    break;
            }
        } else {
            throw new CompilerException("2",Parser.now.line,"VarDef");
        }
    }

    public String getIdentName() {
        return Ident.getDataToken().token;
    }

    public int getDim() {
        return dim;
    }

    public ArrayList<ConstExp> getConst_exp_list() {
        return const_exp_list;
    }

    public InitVal getInitVal() {
        if (InitVal==null)
            return null;
        return (InitVal) InitVal.getDataToken().nt;
    }
}
