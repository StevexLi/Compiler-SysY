package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import Lexer.Token;

import java.util.ArrayList;

/**
 * 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam } // 1.花括号内重复0次 2.花括号内
 * 重复多次
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class FuncFParam extends NonTerminal {
    ASTNode BType;
    ASTNode Ident;
    ArrayList<ASTNode> Params_list = new ArrayList<>();
    FuncFParam() throws Exception {
        this.nt_type = NonTerminalType.FUNCFPARAM;
        BType = new ASTNode(new Token(new BType()));
        if (Parser.now.isIdent()){
            Ident = new ASTNode(Parser.now); // 普通变量
            Parser.lexer.next();
            if (Parser.now.equalLexType(LexType.LBRACK) && Parser.lexer.preRead().equalLexType(LexType.RBRACK)){
                Params_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                Params_list.add(new ASTNode(Parser.now)); // 一维数组
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.LBRACK)){ // 二维数组
                    Params_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    Params_list.add(new ASTNode(new Token(new ConstExp())));
                    if (Parser.now.equalLexType(LexType.RBRACK)){
                        Params_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    }
                }
            }
            setFirstchild(BType);
            BType.setNextSibling(Ident);
            for (int i=0;i<Params_list.size();i++){
                if (i==0)
                    Ident.setNextSibling(Params_list.get(0));
                if (i+1<Params_list.size()){
                    Params_list.get(i).setNextSibling(Params_list.get(i+1));
                }
            }
        }
    }
}
