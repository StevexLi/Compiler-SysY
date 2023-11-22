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

    /**
     * 函数声明块
     *
     * @param FuncType    函数返回值类型
     * @param Ident       函数标识符
     * @param FuncFParams 函数形参列表
     * @throws Exception 例外
     */
    Block(SymbolType FuncType, ASTNode Ident, ArrayList<ASTNode> FuncFParams) throws Exception {
        this.nt_type = NonTerminalType.BLOCK;
        makeTable(FuncType);
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
            int rbrace_line = Parser.prev.line;
            ASTNode last_block_item;
            if (FuncType.equals(SymbolType.RETINT)||FuncType.equals(SymbolType.MAINFUNC)){
                if (BlockItem_list.size()<3){
                    ErrorReporter.reportError(rbrace_line, ErrorType.EG); // fixme:错误处理g
                } else {
                    if ((last_block_item = BlockItem_list.get(BlockItem_list.size()-2))!=null) {
//                        System.out.println((Token)(last_block_item.getData()));
                        if (((BlockItem)(((Token)last_block_item.getData()).nt)).Stmt!=null){
                            ASTNode last_stmt = ((BlockItem)(((Token)last_block_item.getData()).nt)).Stmt;
                            if (!((Stmt)(((Token)last_stmt.getData()).nt)).is_return){
                                ErrorReporter.reportError(rbrace_line, ErrorType.EG); // fixme:错误处理g
                            }
                        } else {
                            ErrorReporter.reportError(rbrace_line, ErrorType.EG); // fixme:错误处理g
                        }
                    } else {
                        ErrorReporter.reportError(rbrace_line, ErrorType.EG); // fixme:错误处理g
                    }
                }
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

    public ArrayList<BlockItem> getBlockItem() {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        for (ASTNode item : BlockItem_list){
            if (!item.getDataToken().equalLexType(LexType.LBRACE) && !item.getDataToken().equalLexType(LexType.RBRACE))
                blockItems.add((BlockItem) item.getDataToken().nt);
        }
        return blockItems;
    }

    void makeTable() {
        Parser.pre = Parser.cur;
        Parser.cur = new SymbolTable(Parser.s_table_list.size(),Parser.pre.getTableId());
        Parser.s_table_list.add(Parser.cur);
    }

    /**
     * 建立函数块的符号表
     *
     * @param FuncType 函数块对应函数的返回值类型
     */
    void makeTable(SymbolType FuncType) {
        Parser.pre = Parser.cur;
        Parser.cur = new SymbolTable(Parser.s_table_list.size(),Parser.pre.getTableId());
        Parser.cur.block_type = FuncType;
        Parser.s_table_list.add(Parser.cur);
    }

    void returnToFatherTable() {
        Parser.cur = Parser.s_table_list.get(Parser.cur.getFatherId()); // 回到上一层表
        if (Parser.cur.getFatherId()==-1)
            return;
        Parser.pre = Parser.s_table_list.get(Parser.cur.getFatherId());
    }
}
