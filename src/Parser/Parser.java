package Parser;

import DataStructure.AST;
import DataStructure.ASTNode;
import Lexer.Lexer;
import Lexer.Token;

import java.util.ArrayList;

public class Parser {
    public static Lexer lexer;
    public static Token now;
    public static Token pre_read;
    public static Token prepre_read;
    public static AST ast;
    public static ASTNode CompUnit;
    ArrayList<Token> token_list;
    ArrayList<Token> ast_post_root_traverse;

    public Parser(Lexer lexer,AST ast, ArrayList<Token> token_list, ArrayList<Token> ast_post_root_traverse) throws Exception {
        Parser.lexer = lexer;
        Parser.ast = ast;
        this.token_list = token_list;
        this.ast_post_root_traverse = ast_post_root_traverse;
        syntaxAnalysis();
        Parser.ast.postRootTraverseToList(ast.getRoot(),ast_post_root_traverse);
    }

    void syntaxAnalysis() throws Exception {
        lexer.next();
        ast.setRoot(new ASTNode(new Token(new CompUnit())));
    }
}
