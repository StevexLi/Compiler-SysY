package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

public class FuncFParams extends NonTerminal {
    ArrayList<ASTNode> FuncFParam_list = new ArrayList<>();
    FuncFParams() throws Exception {
        this.nt_type = NonTerminalType.FUNCFPARAMS;
        FuncFParam_list.add(new ASTNode(new Token(new FuncFParam())));
        while (Parser.now.equalLexType(LexType.COMMA)){
            FuncFParam_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            FuncFParam_list.add(new ASTNode(new Token(new FuncFParam())));
        }
        setFirstchild(FuncFParam_list.get(0));
        for (int i=0;i<FuncFParam_list.size();i++){
            if (i+1<FuncFParam_list.size()){
                FuncFParam_list.get(i).setNextSibling(FuncFParam_list.get(i+1));
            }
        }
    }
}
