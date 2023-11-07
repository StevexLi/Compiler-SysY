package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import Lexer.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 常量初值 ConstInitVal → ConstExp
 * | '{' [ ConstInitVal { ',' ConstInitVal } ] '}' // 1.常表达式初值 2.一维数组初值 3.二
 * 维数组初值
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class ConstInitVal extends NonTerminal {
    ASTNode ConstExp;
    ArrayList<ASTNode> ConstInitVal_list = new ArrayList<>();
    ConstInitVal() throws Exception {
        this.nt_type = NonTerminalType.CONSTINITVAL;
        if (!Parser.now.equalLexType(LexType.LBRACE)){ // ConstInitVal → ConstExp
            ConstExp = new ASTNode(new Token(new ConstExp()));
            setFirstchild(ConstExp);
        } else {
            ConstInitVal_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            if (Parser.now.equalLexType(LexType.RBRACE)){ // {}
                ConstInitVal_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
            } else { //  [ ConstInitVal { ',' ConstInitVal } ]
                ConstInitVal_list.add(new ASTNode(new Token(new ConstInitVal())));
                while (Parser.now.equalLexType(LexType.COMMA)){
                    ConstInitVal_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    ConstInitVal_list.add(new ASTNode(new Token(new ConstInitVal())));
                }
                if (Parser.now.equalLexType(LexType.RBRACE)){
                    ConstInitVal_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    throw new CompilerException("2",Parser.now.line,"ConstInitVal");
                }
            }
            setFirstchild(ConstInitVal_list.get(0));
            for (int i=0;i<ConstInitVal_list.size();i++){
                if (i+1<ConstInitVal_list.size()){
                    ConstInitVal_list.get(i).setNextSibling(ConstInitVal_list.get(i+1));
                }
            }
        }
    }
}
