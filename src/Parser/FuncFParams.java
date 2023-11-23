package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import DataStructure.Token;

import java.util.ArrayList;

/**
 * 函数形参列表 FuncFParams -> FuncFParam { ',' FuncFParam }
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class FuncFParams extends NonTerminal {
    ArrayList<ASTNode> FuncFParam_list = new ArrayList<>();
    int params_num = 0;
    FuncFParams() throws Exception {
        this.nt_type = NonTerminalType.FUNCFPARAMS;
        if (Parser.now.type.equals(LexType.LBRACE))
            return;
        FuncFParam_list.add(new ASTNode(new Token(new FuncFParam())));
        params_num++;
        while (Parser.now.equalLexType(LexType.COMMA)){
            FuncFParam_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            FuncFParam_list.add(new ASTNode(new Token(new FuncFParam())));
            params_num++;
        }
        setFirstchild(FuncFParam_list.get(0));
        for (int i=0;i<FuncFParam_list.size();i++){
            if (i+1<FuncFParam_list.size()){
                FuncFParam_list.get(i).setNextSibling(FuncFParam_list.get(i+1));
            }
        }
    }

    public ArrayList<FuncFParam> getFuncFParam_list() {
        ArrayList<FuncFParam> funcFParam_list = new ArrayList<>();
        for (int i=0; i< FuncFParam_list.size(); i+=2){
            funcFParam_list.add((FuncFParam) FuncFParam_list.get(i).getDataToken().nt);
        }
        return funcFParam_list;
    }

    public int getParams_num() {
        return params_num;
    }
}
