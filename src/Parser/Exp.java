package Parser;

import DataStructure.ASTNode;
import DataStructure.Token;

/**
 * 表达式 Exp → AddExp
 *
 * @author Stevex
 * @date 2023/10/12
 */
public class Exp extends NonTerminal {
    ASTNode AddExp;
    Exp() throws Exception {
        this.nt_type = NonTerminalType.EXP;
        AddExp = new ASTNode(new Token(new AddExp()));
        setFirstchild(AddExp);
    }
}
