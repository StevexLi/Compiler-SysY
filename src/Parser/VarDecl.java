package Parser;

import DataStructure.ASTNode;
import DataStructure.ErrorReporter;
import DataStructure.ErrorType;
import Lexer.LexType;
import DataStructure.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 变量声明 VarDecl → BType VarDef { ',' VarDef } ';' // 1.花括号内重复0次 2.花括号内重复
 * 多次
 * @author Stevex
 * @date 2023/10/13
 */
public class VarDecl extends NonTerminal{
    ASTNode BType;
    ArrayList<ASTNode> VarDef = new ArrayList<>();
    ArrayList<VarDef> VarDefList = new ArrayList<>();
    ASTNode SEMICN;
    VarDecl() throws Exception {
        this.nt_type = NonTerminalType.VARDECL;
        if (Parser.now.equalLexType(LexType.INTTK)){
            BType = new ASTNode(new Token(new BType()));
            VarDef node = new VarDef(LexType.INTTK);
            VarDefList.add(node);
            VarDef.add(new ASTNode(new Token(node)));
            while (Parser.now.equalLexType(LexType.COMMA)) {
                VarDef.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                node = new VarDef(LexType.INTTK);
                VarDefList.add(node);
                VarDef.add(new ASTNode(new Token(node)));
            }
            if (Parser.now.equalLexType(LexType.SEMICN)){
                SEMICN = new ASTNode(Parser.now);
                Parser.lexer.next();
            } else {
//                throw new CompilerException("2",Parser.now.line,"VarDecl");
                ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
            }
        }
        setFirstchild(BType);
        ASTNode node1 = new ASTNode();
        ASTNode node0 = BType;
        for (int i = 0; i< VarDef.size(); i++){
            node1 = VarDef.get(i);
            if (i==0) {
                node0.setNextSibling(node1);
            }
            if (i< VarDef.size()-1)
                node1.setNextSibling(VarDef.get(i+1));
            node0 = node1;
        }
        node0.setNextSibling(SEMICN);
    }

    public LexType getBType() {
        return ((BType) BType.getDataToken().nt).getType();
    }

    public ArrayList<VarDef> getVarDefList() {
        return VarDefList;
    }
}
