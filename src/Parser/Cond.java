package Parser;

import DataStructure.ASTNode;
import DataStructure.Token;

/**
 * 条件表达式 Cond → LOrExp // 存在即可
 * @author Stevex
 * @date 2023/10/14
 */
public class Cond extends NonTerminal {
    ASTNode LOrExp;
    Cond() throws Exception {
        this.nt_type = NonTerminalType.COND;
        LOrExp = new ASTNode(new Token(new LOrExp()));
        setFirstchild(LOrExp);
    }

    public LOrExp getLOrExp(){
        return (LOrExp) this.LOrExp.getDataToken().nt;
    }
}
