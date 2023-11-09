package DataStructure;

import java.util.HashMap;

public class SymbolTable {
    public int id;
    public int father_id;
    public HashMap<String, Symbol> directory = new HashMap<String, Symbol>();

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
    public int checkSymbol_use(Symbol symbol){
        return 0;
    }
}
