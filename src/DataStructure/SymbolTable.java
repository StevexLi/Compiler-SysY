package DataStructure;

import java.util.HashMap;

public class SymbolTable {
    public int id;
    public int father_id;
    public HashMap<String, Symbol> directory = new HashMap<String, Symbol>();
}
