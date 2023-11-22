package Parser;

import DataStructure.AST;
import DataStructure.ASTNode;
import DataStructure.SymbolTable;
import Lexer.Lexer;
import DataStructure.Token;

import java.util.ArrayList;

public class Parser {
    public static Lexer lexer;
    public static Token now;
    public static Token prev; // 上一个Token，用于错误处理行号报告
    public static Token pre_read;
    public static Token prepre_read;
    public static AST ast;
    public static  CompUnit CompUnit;
    ArrayList<Token> token_list;
    ArrayList<Token> ast_post_root_traverse;
    public static ArrayList<SymbolTable> s_table_list = new ArrayList<>();
    public static SymbolTable root;
    public static SymbolTable cur;
    public static SymbolTable pre;

    public Parser(Lexer lexer,AST ast, ArrayList<Token> token_list, ArrayList<Token> ast_post_root_traverse,ArrayList<SymbolTable> s_table_list) throws Exception {
        Parser.lexer = lexer;
        Parser.ast = ast;
        this.token_list = token_list;
        this.ast_post_root_traverse = ast_post_root_traverse;
        Parser.s_table_list = s_table_list;
        syntaxAnalysis();
        Parser.ast.postRootTraverseToList(ast.getRoot(),ast_post_root_traverse);
    }

    void syntaxAnalysis() throws Exception {
        lexer.next();
        CompUnit = new CompUnit();
        ast.setRoot(new ASTNode(new Token(Parser.CompUnit)));
    }

    public CompUnit getCompUnit() {
        return CompUnit;
    }
}
