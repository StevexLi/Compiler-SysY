package DataStructure;

import Parser.*;

import java.util.HashMap;

public class SymbolTable {
    public int id;
    public int father_id;
    public HashMap<String, Symbol> directory = new HashMap<String, Symbol>();
    public SymbolType block_type;

    public SymbolTable(int id, int father_id) {
        this.id = id;
        this.father_id = father_id;
    }

    public int getTableId() {
        return id;
    }

    public int getFatherId() {
        return father_id;
    }

    public boolean addSymbol(Symbol symbol){
        if (directory.containsKey(symbol.token_string)){
            return false;
        }
        directory.put(symbol.token_string, symbol);
        return true;
    }

    /**
     * 以字符串为键检查符号定义
     * 若在本层无定义，返回false
     *
     * @param symbol_string 符号字符串
     * @return boolean
     */
    public boolean checkSymbol_def_string(String symbol_string){
        return directory.containsKey(symbol_string);
    }

    /**
     * 检查被使用符号是否被定义过
     *
     * @param symbol_string 符号字符串
     * @return int 定义该符号的表号，若没有被定义，返回-1
     */
    public int checkSymbol_use_string(String symbol_string){
        int cur = id;
        SymbolTable symbolTable;
        int defined_in_table_id;
        boolean defined = false;
        while (cur!=-1){
            symbolTable = Parser.s_table_list.get(cur);
            defined = symbolTable.checkSymbol_def_string(symbol_string);
            if (defined)
                break;
            cur = symbolTable.getFatherId();
        }
        return cur;
    }

    public boolean checkSymbol_const_string(String symbol_string) {
        int ident_table = checkSymbol_use_string(symbol_string);
        if (ident_table>=0){
            return Parser.s_table_list.get(ident_table).directory.get(symbol_string).is_const;
        }
        return false;
    }

    public boolean checkSymbolTable_return_type(SymbolType ret_type){
        int cur = id;
        SymbolTable symbolTable;
        boolean is_func = false;
        while (cur!=-1){
            symbolTable = Parser.s_table_list.get(cur);
            if (symbolTable.block_type!=null){ // !
                if (symbolTable.block_type.equals(SymbolType.MAINFUNC)){
                    if (ret_type.equals(SymbolType.RETINT))
                        return true;
                }
                return symbolTable.block_type.equals(ret_type);
            }
            cur = symbolTable.getFatherId();
        }
        return false;
    }

    /**
     * 检查函数参数个数是否匹配
     *
     * @param ident          函数标识符
     * @param FuncRParamList 函数实参列表
     * @return boolean
     */
    public boolean checkFuncParamNum(ASTNode ident, ASTNode FuncRParamList){
        String ident_string = ((Token)(ident.getData())).token;
        if (!Parser.root.checkSymbol_def_string(ident_string)){ // 根本未定义
            return false;
        }
        Symbol func_def = Parser.root.directory.get(ident_string);
        if (FuncRParamList==null){
            return func_def.param_num == 0;
        } else {
            FuncRParams funcRParams = (FuncRParams)((Token)FuncRParamList.getData()).nt;
            return func_def.param_num == funcRParams.getParamNum();
        }
    }

    /**
     * 检查函数参数类型是否匹配
     *
     * @param ident          函数标识符
     * @param FuncRParamList 函数实参列表
     * @return boolean
     */
    public boolean checkFuncParamType(ASTNode ident, ASTNode FuncRParamList){
        return true;
    }

    public int checkSymbol_use(Symbol symbol){
        return 0;
    }
}
