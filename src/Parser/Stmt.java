package Parser;

import DataStructure.ASTNode;
import Lexer.LexType;
import Lexer.Token;
import Exception.*;

import java.util.ArrayList;

/**
 * 语句 Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
 * | [Exp] ';' //有无Exp两种情况
 * | Block
 * | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
 * | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个
 * ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
 * | 'break' ';' | 'continue' ';'
 * | 'return' [Exp] ';' // 1.有Exp 2.无Exp
 * | LVal '=' 'getint''('')'';'
 * | 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
 *
 * @author Stevex
 * @date 2023/10/13
 */
public class Stmt extends NonTerminal {
    String Stmt_type;
    ArrayList<ASTNode> Stmt_list = new ArrayList<>();
    Stmt() throws Exception {
        this.nt_type = NonTerminalType.STMT;
        switch (Parser.now.type){
            case LBRACE: // Block
                Stmt_list.add(new ASTNode(new Token(new Block())));
                break;
            case IFTK: // 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
                Stmt_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.LPARENT)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    Stmt_list.add(new ASTNode(new Token(new Cond())));
                    if (Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                        Stmt_list.add(new ASTNode(new Token(new Stmt())));
                        if (Parser.now.equalLexType(LexType.ELSETK)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                            Stmt_list.add(new ASTNode(new Token(new Stmt())));
                        }
                    } else {
                        throw new CompilerException("2",Parser.now.line, "Stmt_if2");
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_if");
                }
                break;
            case FORTK: // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
                Stmt_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.LPARENT)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    if (!Parser.now.equalLexType(LexType.SEMICN)){
                        Stmt_list.add(new ASTNode(new Token(new ForStmt())));
                    }
                    if (Parser.now.equalLexType(LexType.SEMICN)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
                        throw new CompilerException("2",Parser.now.line, "Stmt_for1");
                    }
                    if (!Parser.now.equalLexType(LexType.SEMICN)){
                        Stmt_list.add(new ASTNode(new Token(new Cond())));
                    }
                    if (Parser.now.equalLexType(LexType.SEMICN)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
                        throw new CompilerException("2",Parser.now.line, "Stmt_for2");
                    }
                    if (!Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(new Token(new ForStmt())));
                    }
                    if (Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
                        throw new CompilerException("2",Parser.now.line, "Stmt_for3");
                    }
                    Stmt_list.add(new ASTNode(new Token(new Stmt())));
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_for");
                }
                break;
            case BREAKTK: //  'break' ';'
                Stmt_type = "BREAK";
                Stmt_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_break");
                }
                break;
            case CONTINUETK: // 'continue' ';'
                Stmt_type = "CONTINUE";
                Stmt_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_continue");
                }
                break;
            case RETURNTK: // 'return' [Exp] ';' // 1.有Exp 2.无Exp
                Stmt_type = "RETURN";
                Stmt_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (!Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(new Token(new Exp())));
                }
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_return");
                }
                break;
            case PRINTFTK: // 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
                Stmt_type = "PRINTF";
                Stmt_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.LPARENT)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    Stmt_list.add(new ASTNode(Parser.now)); // FormatString
                    Parser.lexer.next();
                    while (Parser.now.equalLexType(LexType.COMMA)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                        Stmt_list.add(new ASTNode(new Token(new Exp())));
                    }
                    if (Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                        if (Parser.now.equalLexType(LexType.SEMICN)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                        } else {
                            throw new CompilerException("2",Parser.now.line, "Stmt_printf3");
                        }
                    } else {
                        throw new CompilerException("2",Parser.now.line, "Stmt_printf2");
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_printf1");
                }
                break;
            case LPARENT:
            case INTCON:
            case PLUS:
            case MINU:
            case NOT:
            case SEMICN: // [Exp] ';' // FIRST={(,Number,Ident,+,-,!}
                if (!Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(new Token(new Exp())));
                }
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_[Exp];");
                }
                break;
            case IDENFR: //  Stmt → LVal '=' Exp ';'    // FIRST={Ident} LVal '=' 'getint''('')'';'    // FIRST={Ident} | [Exp] ';'     // FIRST={(,Number,Ident,+,-,!}
                ASTNode node1 = new ASTNode(new Token(new Exp()));
                if (Parser.now.equalLexType(LexType.SEMICN)){ // [Exp] ';'     // FIRST={(,Number,Ident,+,-,!}
                    Stmt_list.add(node1);
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else if (Parser.now.equalLexType(LexType.ASSIGN)){
                    ASTNode node2 = node1;
                    for (int i=0;i<5;i++){
                        node2 = node2.getFirstChild();
                        if (node2==null)
                            throw new CompilerException("2",Parser.now.line, "Stmt_ident2");
                    }
                    Stmt_list.add(node2);
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    if (Parser.now.equalLexType(LexType.GETINTTK)){ // LVal '=' 'getint''('')'';'    // FIRST={Ident}
                        Stmt_list.add(new ASTNode(Parser.now)); // getint
                        Parser.lexer.next();
                        if (Parser.now.equalLexType(LexType.LPARENT)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                            if (Parser.now.equalLexType(LexType.RPARENT)){
                                Stmt_list.add(new ASTNode(Parser.now));
                                Parser.lexer.next();
                                if (Parser.now.equalLexType(LexType.SEMICN)){
                                    Stmt_list.add(new ASTNode(Parser.now));
                                    Parser.lexer.next();
                                }
                            }
                        }
                    } else { // LVal '=' Exp ';'    // FIRST={Ident}
                        Stmt_list.add(new ASTNode(new Token(new Exp())));
                        if (Parser.now.equalLexType(LexType.SEMICN)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                        }
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_ident;");
                }
                break;
            default: // 啥都不是
                throw new CompilerException("2",Parser.now.line, "Stmt_nothing;");
        }
        for (int i=0;i<Stmt_list.size();i++){
            ASTNode node1 = Stmt_list.get(i);
            if (i==0)
                setFirstchild(node1);
            if (i+1<Stmt_list.size())
                node1.setNextSibling(Stmt_list.get(i+1));
        }
    }
}
