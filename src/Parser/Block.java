package Parser;

import Lexer.LexType;
import Lexer.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 语句块 Block → '{' { BlockItem } '}' // 1.花括号内重复0次 2.花括号内重复多次
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class Block extends NonTerminal {
    ArrayList<ASTNode> BlockItem_list = new ArrayList<>();

    Block() throws Exception {
        this.nt_type = NonTerminalType.BLOCK;
        if (Parser.now.equalLexType(LexType.LBRACE)) {
            BlockItem_list.add(new ASTNode(Parser.now));
            Parser.lexer.next();
            while (!Parser.now.equalLexType(LexType.RBRACE)) { // { BlockItem }
                if (Parser.now.is_end)
                    throw new CompilerException("2", Parser.now.line, "Block");
                BlockItem_list.add(new ASTNode(new Token(new BlockItem())));
            }
            if (Parser.now.equalLexType(LexType.RBRACE)) {
                BlockItem_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
            }
            for (int i=0;i<BlockItem_list.size();i++){
                setFirstchild(BlockItem_list.get(i));
                if (i+1<BlockItem_list.size()){
                    BlockItem_list.get(i).setNextSibling(BlockItem_list.get(i+1));
                }
            }
        }
    }
}
