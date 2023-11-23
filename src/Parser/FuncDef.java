package Parser;

import DataStructure.*;
import Lexer.LexType;

import java.util.ArrayList;

/**
 * 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // 1.无形参 2.有形参
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class FuncDef extends NonTerminal {
    ASTNode FuncType;
    ASTNode Ident;
    ArrayList<ASTNode> FuncFParams = new ArrayList<>();
    FuncFParams funcFParams;
    ASTNode Block;
    int params_num;
    ArrayList<ASTNode> params = new ArrayList<>();
    FuncDef() throws Exception{
        this.nt_type = NonTerminalType.FUNCDEF;
        FuncType = new ASTNode(new Token(new FuncType()));
        SymbolType func_type;
        if (((FuncType)(((Token) FuncType.getData()).nt)).type.equals(LexType.INTTK)){
            func_type = SymbolType.RETINT;
        } else {
            func_type = SymbolType.RETVOID;
        }
        if (Parser.now.isIdent()){
            Ident = new ASTNode(Parser.now);
            int ident_line = Parser.now.line;
            if (Parser.cur.checkSymbol_def_string(Parser.now.token)){
                ErrorReporter.reportError(ident_line, ErrorType.EB); // fixme:错误处理b
            }
            Parser.lexer.next();
            if (Parser.now.equalLexType(LexType.LPARENT)) {
                FuncFParams.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (!Parser.now.equalLexType(LexType.RPARENT)){
                    funcFParams = new FuncFParams();
                    FuncFParams.add(new ASTNode(new Token(funcFParams)));
                }
                if (Parser.now.equalLexType(LexType.RPARENT)){
                    FuncFParams.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
                    FuncFParams.add(new ASTNode(new Token(")",LexType.RPARENT,Parser.prev.line)));
                }


                if (FuncFParams.size()==2){
                    params_num = 0;
                } else {
                    params_num = ((FuncFParams)(((Token)FuncFParams.get(1).getData()).nt)).params_num;
                    ArrayList<ASTNode> params_list = ((FuncFParams)(((Token)FuncFParams.get(1).getData()).nt)).FuncFParam_list;
                    for(int i=0;i<2*params_num;i+=2){
                        params.add(params_list.get(i));
                    }
                }
                Parser.cur.addSymbol(new Symbol(Ident, SymbolType.FUNC, func_type, params_num, params));
                Block = new ASTNode(new Token(new Block(func_type, Ident, params)));
                setFirstchild(FuncType);
                FuncType.setNextSibling(Ident);
                ASTNode node1 = new ASTNode();
                ASTNode node0 = Ident;
                for (int i=0;i<FuncFParams.size();i++){
                    node1 = FuncFParams.get(i);
                    if (i==0){
                        node0.setNextSibling(node1);
                    }
                    if (i<FuncFParams.size()-1)
                        node1.setNextSibling(FuncFParams.get(i+1));
                    node0 = node1;
                }
                node1.setNextSibling(Block);
            }
        }
    }

    public LexType getFuncType() {
        return ((FuncType) FuncType.getDataToken().nt).getType();
    }
    public String getIdentString() {
        return Ident.getDataToken().token;
    }
    public FuncFParams getFuncFParams() {
        return funcFParams;
    }
    public Block getBlock() {
        return (Block) Block.getDataToken().nt;
    }
}
