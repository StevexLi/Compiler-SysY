package Parser;

import DataStructure.*;
import Lexer.LexType;
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
    StmtType stmt_type;
    ArrayList<ASTNode> Stmt_list = new ArrayList<>();
    boolean in_for = false; // 标识是否位于for循环中
    SymbolType return_type;
    boolean is_return = false;
    boolean has_return_exp = true;
    LVal lval;
    Exp exp_single;
    Block block;

    public enum StmtType {
        LVALASSIGNEXP,
        EXP,
        BLOCK,
        IF,
        FOR,
        BREAK,
        CONTINUE,
        RETURN,
        LVALASSIGNGETINT,
        PRINTF
    }

    Stmt(boolean in_for_loop) throws Exception {
        this.nt_type = NonTerminalType.STMT;
        this.in_for = in_for_loop;
        switch (Parser.now.type){
            case LBRACE: // Block
                stmt_type = StmtType.BLOCK;
                block = new Block(in_for);
                Stmt_list.add(new ASTNode(new Token(block)));
                break;
            case IFTK: // 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
                stmt_type = StmtType.IF;
                Stmt_list.add(new ASTNode(Parser.now));
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.LPARENT)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    Stmt_list.add(new ASTNode(new Token(new Cond())));
                    if (Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                        Stmt_list.add(new ASTNode(new Token(new Stmt(in_for))));
                        if (Parser.now.equalLexType(LexType.ELSETK)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                            Stmt_list.add(new ASTNode(new Token(new Stmt(in_for))));
                        }
                    } else {
//                        throw new CompilerException("2",Parser.now.line, "Stmt_if2");
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
                        Stmt_list.add(new ASTNode(new Token(new Stmt(in_for))));
                        if (Parser.now.equalLexType(LexType.ELSETK)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                            Stmt_list.add(new ASTNode(new Token(new Stmt(in_for))));
                        }
                    }
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_if");
                }
                break;
            case FORTK: // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
                stmt_type = StmtType.FOR;
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
//                        throw new CompilerException("2",Parser.now.line, "Stmt_for1");
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                    }
                    if (!Parser.now.equalLexType(LexType.SEMICN)){
                        Stmt_list.add(new ASTNode(new Token(new Cond())));
                    }
                    if (Parser.now.equalLexType(LexType.SEMICN)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
//                        throw new CompilerException("2",Parser.now.line, "Stmt_for2");
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                    }
                    if (!Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(new Token(new ForStmt())));
                    }
                    if (Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                    } else {
//                        throw new CompilerException("2",Parser.now.line, "Stmt_for3");
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
                    }
                    Stmt_list.add(new ASTNode(new Token(new Stmt(true))));
                } else {
                    throw new CompilerException("2",Parser.now.line, "Stmt_for");
                }
                break;
            case BREAKTK: //  'break' ';'
                stmt_type = StmtType.BREAK;
                Stmt_type = "BREAK";
                Stmt_list.add(new ASTNode(Parser.now));
                int break_line = Parser.now.line;
                if (!in_for){
                    ErrorReporter.reportError(break_line,ErrorType.EM); // fixme:错误处理m
                }
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
//                    throw new CompilerException("2",Parser.now.line, "Stmt_break");
                    ErrorReporter.reportError(break_line,ErrorType.EI); // fixme:错误处理i
                }
                break;
            case CONTINUETK: // 'continue' ';'
                stmt_type = StmtType.CONTINUE;
                Stmt_type = "CONTINUE";
                Stmt_list.add(new ASTNode(Parser.now));
                int continue_line = Parser.now.line;
                if (!in_for){
                    ErrorReporter.reportError(continue_line,ErrorType.EM); // fixme:错误处理m
                }
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
//                    throw new CompilerException("2",Parser.now.line, "Stmt_continue");
                    ErrorReporter.reportError(continue_line,ErrorType.EI); // fixme:错误处理i
                }
                break;
            case RETURNTK: // 'return' [Exp] ';' // 1.有Exp 2.无Exp
                stmt_type = StmtType.RETURN;
                Stmt_type = "RETURN";
                Stmt_list.add(new ASTNode(Parser.now));
                is_return = true;
                int return_line = Parser.now.line;
                int return_token_line = Parser.now.line;
                Parser.lexer.next();
                return_type = SymbolType.RETVOID;
                if (!Parser.now.equalLexType(LexType.SEMICN)){
                    exp_single = new Exp();
                    Stmt_list.add(new ASTNode(new Token(exp_single)));
                    return_line = Parser.prev.line;
                    return_type = SymbolType.RETINT;
                }
                if (!Parser.cur.checkSymbolTable_return_type(return_type)){
                    ErrorReporter.reportError(return_token_line, ErrorType.EF); // fixme:错误处理f
                }
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
//                    throw new CompilerException("2",Parser.now.line, "Stmt_return");
                    ErrorReporter.reportError(return_line, ErrorType.EI); // fixme:错误处理i
                }
                break;
            case PRINTFTK: // 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
                stmt_type = StmtType.PRINTF;
                Stmt_type = "PRINTF";
                Stmt_list.add(new ASTNode(Parser.now));
                int printf_line = Parser.now.line;
                Parser.lexer.next();
                if (Parser.now.equalLexType(LexType.LPARENT)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    int d = Parser.now.value; // FormatString里%d的个数
                    int real_d = 0; // printf里参数个数
                    Stmt_list.add(new ASTNode(Parser.now)); // FormatString
                    Parser.lexer.next();
                    while (Parser.now.equalLexType(LexType.COMMA)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                        Stmt_list.add(new ASTNode(new Token(new Exp())));
                        real_d++;
                    }
                    if (d!=real_d){
                        ErrorReporter.reportError(printf_line, ErrorType.EL); // fixme:错误处理l
                    }
                    if (Parser.now.equalLexType(LexType.RPARENT)){
                        Stmt_list.add(new ASTNode(Parser.now));
                        Parser.lexer.next();
                        if (Parser.now.equalLexType(LexType.SEMICN)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                        } else {
//                            throw new CompilerException("2",Parser.now.line, "Stmt_printf3");
                            ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                        }
                    } else {
//                        throw new CompilerException("2",Parser.now.line, "Stmt_printf2");
                        ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
                        if (Parser.now.equalLexType(LexType.SEMICN)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                        } else {
//                            throw new CompilerException("2",Parser.now.line, "Stmt_printf3");
                            ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                        }
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
                stmt_type = StmtType.EXP;
                if (!Parser.now.equalLexType(LexType.SEMICN)){
                    Exp e = new Exp();
                    exp_single = e;
                    Stmt_list.add(new ASTNode(new Token(e)));
                }
                if (Parser.now.equalLexType(LexType.SEMICN)){
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else {
//                    throw new CompilerException("2",Parser.now.line, "Stmt_[Exp];");
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                }
                break;
            case IDENFR: //  Stmt → LVal '=' Exp ';'    // FIRST={Ident} LVal '=' 'getint''('')'';'    // FIRST={Ident} | [Exp] ';'     // FIRST={(,Number,Ident,+,-,!}
                ASTNode node1 = new ASTNode(new Token(new Exp()));
                if (Parser.now.equalLexType(LexType.SEMICN)){ // [Exp] ';'     // FIRST={(,Number,Ident,+,-,!}
                    stmt_type = StmtType.EXP;
                    Stmt_list.add(node1);
                    exp_single = (Exp) node1.getDataToken().nt;
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                } else if (Parser.now.equalLexType(LexType.ASSIGN)){
                    ASTNode node2 = node1;
                    for (int i=0;i<5;i++){ // 提取出Exp内的LVal
                        node2 = node2.getFirstChild();
                        if (node2==null)
                            throw new CompilerException("2",Parser.now.line, "Stmt_ident2");
                    }
                    Stmt_list.add(node2);
                    lval = (LVal) node2.getDataToken().nt;
                    String ident_string = ((LVal)((Token)node2.getData()).nt).getIdentString();
                    int ident_line = ((LVal)((Token)node2.getData()).nt).getIdentLine();
                    if (Parser.cur.checkSymbol_const_string(ident_string)){
                        ErrorReporter.reportError(ident_line, ErrorType.EH); // fixme:错误处理h
                    }
                    Stmt_list.add(new ASTNode(Parser.now));
                    Parser.lexer.next();
                    if (Parser.now.equalLexType(LexType.GETINTTK)){ // LVal '=' 'getint''('')'';'    // FIRST={Ident}
                        stmt_type = StmtType.LVALASSIGNGETINT;
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
                                } else {
                                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                                }
                            } else {
                                ErrorReporter.reportError(Parser.prev.line, ErrorType.EJ); // fixme:错误处理j
                                if (Parser.now.equalLexType(LexType.SEMICN)){
                                    Stmt_list.add(new ASTNode(Parser.now));
                                    Parser.lexer.next();
                                } else {
                                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                                }
                            }
                        }
                    } else { // LVal '=' Exp ';'    // FIRST={Ident}
                        stmt_type = StmtType.LVALASSIGNEXP;
                        Exp e = new Exp();
                        exp_single = e;
                        Stmt_list.add(new ASTNode(new Token(e)));
                        if (Parser.now.equalLexType(LexType.SEMICN)){
                            Stmt_list.add(new ASTNode(Parser.now));
                            Parser.lexer.next();
                        } else {
                            ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
                        }
                    }
                } else {
//                    throw new CompilerException("2",Parser.now.line, "Stmt_ident;");
                    ErrorReporter.reportError(Parser.prev.line, ErrorType.EI); // fixme:错误处理i
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

    public StmtType getStmt_type() {
        return stmt_type;
    }

    public LVal getLval() {
        return lval;
    }

    public Exp getExp_single() {
        return exp_single;
    }

    public Block getBlock() {
        return block;
    }
}
