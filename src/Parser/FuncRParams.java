package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

public class FuncRParams extends NonTerminal {
    ArrayList<ASTNode> Exp_list = new ArrayList<>();
    int param_num = 0;
    FuncRParams() throws Exception {
        this.nt_type = NonTerminalType.FUNCRPARAMS;
        Exp_list.add(new ASTNode(new Token(new Exp())));
        param_num++;
        while(Parser.now.equalLexType(LexType.COMMA)){
            Exp_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            Exp_list.add(new ASTNode(new Token(new Exp())));
            param_num++;
        }
        setFirstchild(Exp_list.get(0));
        for (int i=0;i<Exp_list.size();i++){
            if (i+1<Exp_list.size()){
                Exp_list.get(i).setNextSibling(Exp_list.get(i+1));
            }
        }
    }
    public int getParamNum(){
        return param_num;
    }
}
