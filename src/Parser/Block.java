package Parser;

import DataStructure.*;
import Lexer.LexType;
import Exception.*;

import java.util.ArrayList;

/**
 * 语句块 Block → '{' { BlockItem } '}' // 1.花括号内重复0次 2.花括号内重复多次
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class Block extends NonTerminal {
    ArrayList<ASTNode> BlockItem_list = new ArrayList<>();
    boolean in_for = false;
    Block() throws Exception {
        this.nt_type = NonTerminalType.BLOCK;
        makeTable();
        if (Parser.now.equalLexType(LexType.LBRACE)) {
            BlockItem_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            while (!Parser.now.equalLexType(LexType.RBRACE)) { // { BlockItem }
                if (Parser.now.is_end)
                    throw new CompilerException("2", Parser.now.line, "Block");
                BlockItem_list.add(new ASTNode(new Token(new BlockItem(in_for))));
            }
            if (Parser.now.equalLexType(LexType.RBRACE)) {
                BlockItem_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
            }
            for (int i=0;i<BlockItem_list.size();i++){
                setFirstchild(BlockItem_list.get(i));
                if (i+1<BlockItem_list.size()){
                    BlockItem_list.get(i).setNextSibling(BlockItem_list.get(i+1));
                }
            }
        }
        returnToFatherTable();
    }

    Block(boolean in_for_loop) throws Exception {
        this.nt_type = NonTerminalType.BLOCK;
        makeTable();
        this.in_for = in_for_loop;
        if (Parser.now.equalLexType(LexType.LBRACE)) {
            BlockItem_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            while (!Parser.now.equalLexType(LexType.RBRACE)) { // { BlockItem }
                if (Parser.now.is_end)
                    throw new CompilerException("2", Parser.now.line, "Block");
                BlockItem_list.add(new ASTNode(new Token(new BlockItem(in_for))));
            }
            if (Parser.now.equalLexType(LexType.RBRACE)) {
                BlockItem_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
            }
            for (int i=0;i<BlockItem_list.size();i++){
                setFirstchild(BlockItem_list.get(i));
                if (i+1<BlockItem_list.size()){
                    BlockItem_list.get(i).setNextSibling(BlockItem_list.get(i+1));
                }
            }
        }
        returnToFatherTable();
    }

    Block(ASTNode FuncType, ASTNode Ident, ArrayList<ASTNode> FuncFParams) throws Exception {
        this.nt_type = NonTerminalType.BLOCK;
        makeTable();
        for (ASTNode param: FuncFParams){
            ASTNode ident = ((FuncFParam)(((Token)param.getData()).nt)).getIdent();
            Token ident_token = ((Token)ident.getData());
            int ident_line = ((Token)ident.getData()).line;
            if (Parser.cur.checkSymbol_def_string(ident_token.token)){
                ErrorReporter.reportError(ident_line, ErrorType.EB); // fixme:错误处理b
            }
            LexType var_type = ((FuncFParam)(((Token)param.getData()).nt)).getType();
            int dim = ((FuncFParam)(((Token)param.getData()).nt)).getDim();
            switch (dim){
                case 0: // 普通变量
                    Parser.cur.addSymbol(new Symbol(ident,SymbolType.VAR,var_type,null));
                    break;
                case 1: // 一维数组
                    Parser.cur.addSymbol(new Symbol(ident,SymbolType.DIM1ARRAY,var_type,null));
                    break;
                case 2: // 二维数组
                    ASTNode const_exp = ((FuncFParam)(((Token)param.getData()).nt)).getConst_exp();
                    Parser.cur.addSymbol(new Symbol(ident,SymbolType.DIM2ARRAY,var_type,const_exp));
                    break;
            }
        }
        if (Parser.now.equalLexType(LexType.LBRACE)) {
            BlockItem_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            while (!Parser.now.equalLexType(LexType.RBRACE)) { // { BlockItem }
                if (Parser.now.is_end)
                    throw new CompilerException("2", Parser.now.line, "Block");
                BlockItem_list.add(new ASTNode(new Token(new BlockItem(in_for))));
            }
            if (Parser.now.equalLexType(LexType.RBRACE)) {
                BlockItem_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
            }
            for (int i=0;i<BlockItem_list.size();i++){
                setFirstchild(BlockItem_list.get(i));
                if (i+1<BlockItem_list.size()){
                    BlockItem_list.get(i).setNextSibling(BlockItem_list.get(i+1));
                }
            }
        }
        returnToFatherTable();
    }

    void makeTable() {
        Parser.pre = Parser.cur;
        Parser.cur = new SymbolTable(Parser.s_table_list.size(),Parser.pre.getTableId());
        Parser.s_table_list.add(Parser.cur);
    }

    void returnToFatherTable() {
        Parser.cur = Parser.s_table_list.get(Parser.cur.getFatherId()); // 回到上一层表
        if (Parser.cur.getFatherId()==-1)
            return;
        Parser.pre = Parser.s_table_list.get(Parser.cur.getFatherId());
    }
}
