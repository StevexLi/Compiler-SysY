package Lexer;

import DataStructure.ErrorReporter;
import DataStructure.ErrorType;
import DataStructure.Token;
import Exception.*;
import Parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 词法分析器
 *
 * @author Stevex
 * @date 2023/09/22
 */
public class Lexer {
    String source;
    ArrayList<Token> token_list;
    int curPos = 0;
    int source_length;
    StringBuffer token = new StringBuffer();
    LexType lexType;
    static HashMap<String, LexType> reserveWords = new HashMap<>();
    int lineNum = 1;
    public int curIndex = -1;

    /**
     * 词法分析器构造函数
     *
     * @param source_code_string 源代码字符串
     * @param token_list         token列表
     * @throws Exception 异常
     */
    public Lexer(String source_code_string, ArrayList<Token> token_list) throws Exception {
        this.source = source_code_string;
        this.token_list = token_list;
        buildReserveWordsMap();
        lexicalAnalysis();
    }

    /**
     * 建立保留词表
     */
    void buildReserveWordsMap() {
        LexType.buildReserveWordsMap(reserveWords);
    }

    /**
     * 词法分析
     *
     * @throws Exception 异常
     */
    void lexicalAnalysis() throws Exception {
        source_length = source.length();
        while (curPos<source_length) {
            nextWord();
        }
    }

    /**
     * 解析下一个单词
     *
     * @throws Exception 异常
     */
    public void nextWord() throws Exception {
        char c;
        String s;
        token.setLength(0); // 清空StringBuffer
        // 跳过空白符并在换行处增加当前行数
        while(curPos<source_length && Character.isWhitespace(source.charAt(curPos))) {
            if (source.charAt(curPos) == '\n') {
                lineNum++;
            }
            curPos++;
        }
        // 跳过注释并在换行处增加当前行数
        if (curPos<source_length && source.charAt(curPos) == '/') {
            // 跳过单行注释
            if ((curPos+1)<source_length && source.charAt(curPos+1) == '/') {
                curPos += 2;
                while (curPos<source_length) {
                    if (source.charAt(curPos) == '\n') {
                        lineNum++;
                        curPos++;
                        break; // 遇到换行符，单行注释结束
                    }
                    curPos++;
                }
                return; // TODO: reconsider this
            // 跳过多行注释
            } else if((curPos+1)<source_length && source.charAt(curPos+1) == '*') {
                curPos += 2;
                while (curPos<source_length) {
                    if (source.charAt(curPos) == '\n') {
                        lineNum++;
                    } else if (source.charAt(curPos) == '*' && (curPos+1)<source_length) {
                        if (source.charAt(curPos+1) == '/') {
                            curPos += 2;
                            break; // 遇到注释结束标记
                        }
                    }
                    curPos++;
                }
                return; // TODO: reconsider this
            }
        }
        // 先把字符存入c，方便后续比较，已到结尾则直接返回
        if (curPos<source_length)
            c = source.charAt(curPos);
        else
            return; // TODO: reconsider this
        // 字母或下划线开头，判断是保留字还是标识符
        if (Character.isLetter(c) || c == '_') {
            token.append(c);
            curPos++;
            while (curPos<source_length) {
                c = source.charAt(curPos);
                if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
                    token.append(c);
                } else {
                    break;
                }
                curPos++;
            }
            s = token.toString();
            token.setLength(0);
            // 是保留字
            if (isReserveWord(s)) {
                token_list.add(new Token(s,reserveWords.get(s),lineNum));
            } else { // 是标识符
                token_list.add(new Token(s,LexType.IDENFR,lineNum));
            }
        } else if(Character.isDigit(c)) { // 数字开头，是常数
            token.append(c);
            curPos++;
            while (curPos<source_length) {
                c = source.charAt(curPos);
                if (Character.isDigit(c)) {
                    token.append(c);
                } else {
                    break;
                }
                curPos++;
            }
            s = token.toString();
            token.setLength(0);
            token_list.add(new Token(s,LexType.INTCON,lineNum,Integer.parseInt(s)));
        } else {
            switch (c) {
                case '!': // !或!=
                    if ((curPos+1)<source_length && source.charAt(curPos+1) == '=') {
                        curPos += 2;
                        // !=
                        token_list.add(new Token(LexType.NEQ.lexEnumGetWord(),LexType.NEQ,lineNum));
                    } else {
                        // !
                        curPos++;
                        token_list.add(new Token(LexType.NOT.lexEnumGetWord(),LexType.NOT,lineNum));
                    }
                    break;
                case '&': // &&
                    if ((curPos+1)<source_length && source.charAt(curPos+1) == '&') {
                        curPos += 2;
                        token_list.add(new Token(LexType.AND.lexEnumGetWord(),LexType.AND,lineNum));
                    } else {
                        throw new CompilerException("invalid token:'&'"); // TODO: exception_
                    }
                    break;
                case '|': // ||
                    if ((curPos+1)<source_length && source.charAt(curPos+1) == '|') {
                        curPos += 2;
                        token_list.add(new Token(LexType.OR.lexEnumGetWord(),LexType.OR,lineNum));
                    } else {
                        throw new CompilerException("invalid token:'|'"); // TODO: exception_
                    }
                    break;
                case '+':
                    curPos++;
                    token_list.add(new Token(LexType.PLUS.lexEnumGetWord(),LexType.PLUS,lineNum));
                    break;
                case '-':
                    curPos++;
                    token_list.add(new Token(LexType.MINU.lexEnumGetWord(),LexType.MINU,lineNum));
                    break;
                case '*':
                    curPos++;
                    token_list.add(new Token(LexType.MULT.lexEnumGetWord(),LexType.MULT,lineNum));
                    break;
                case '/':
                    curPos++;
                    token_list.add(new Token(LexType.DIV.lexEnumGetWord(),LexType.DIV,lineNum));
                    break;
                case '%':
                    curPos++;
                    token_list.add(new Token(LexType.MOD.lexEnumGetWord(),LexType.MOD,lineNum));
                    break;
                case '<': // <或<=
                    if ((curPos+1)<source_length && source.charAt(curPos+1) == '=') {
                        curPos += 2;
                        // <=
                        token_list.add(new Token(LexType.LEQ.lexEnumGetWord(),LexType.LEQ,lineNum));
                    } else {
                        // <
                        curPos++;
                        token_list.add(new Token(LexType.LSS.lexEnumGetWord(),LexType.LSS,lineNum));
                    }
                    break;
                case '>': // >或>=
                    if ((curPos+1)<source_length && source.charAt(curPos+1) == '=') {
                        curPos += 2;
                        // >=
                        token_list.add(new Token(LexType.GEQ.lexEnumGetWord(),LexType.GEQ,lineNum));
                    } else {
                        // >
                        curPos++;
                        token_list.add(new Token(LexType.GRE.lexEnumGetWord(),LexType.GRE,lineNum));
                    }
                    break;
                case '=': // =或==
                    if ((curPos+1)<source_length && source.charAt(curPos+1) == '=') {
                        curPos += 2;
                        // ==
                        token_list.add(new Token(LexType.EQL.lexEnumGetWord(),LexType.EQL,lineNum));
                    } else {
                        // =
                        curPos++;
                        token_list.add(new Token(LexType.ASSIGN.lexEnumGetWord(),LexType.ASSIGN,lineNum));
                    }
                    break;
                case ';':
                    curPos++;
                    token_list.add(new Token(LexType.SEMICN.lexEnumGetWord(),LexType.SEMICN,lineNum));
                    break;
                case ',':
                    curPos++;
                    token_list.add(new Token(LexType.COMMA.lexEnumGetWord(),LexType.COMMA,lineNum));
                    break;
                case '(':
                    curPos++;
                    token_list.add(new Token(LexType.LPARENT.lexEnumGetWord(),LexType.LPARENT,lineNum));
                    break;
                case ')':
                    curPos++;
                    token_list.add(new Token(LexType.RPARENT.lexEnumGetWord(),LexType.RPARENT,lineNum));
                    break;
                case '[':
                    curPos++;
                    token_list.add(new Token(LexType.LBRACK.lexEnumGetWord(),LexType.LBRACK,lineNum));
                    break;
                case ']':
                    curPos++;
                    token_list.add(new Token(LexType.RBRACK.lexEnumGetWord(),LexType.RBRACK,lineNum));
                    break;
                case '{':
                    curPos++;
                    token_list.add(new Token(LexType.LBRACE.lexEnumGetWord(),LexType.LBRACE,lineNum));
                    break;
                case '}':
                    curPos++;
                    token_list.add(new Token(LexType.RBRACE.lexEnumGetWord(),LexType.RBRACE,lineNum));
                    break;
                case '"': // FormatString
                    token.append(c);
                    curPos++;
                    int d = 0;
                    while (curPos<source_length) {
                        c = source.charAt(curPos);
                        if (isNormalChar(c)) { // NormalChar
                            if (c == '\\' && (curPos+1)<source_length && source.charAt(curPos+1) == 'n') {
                                token.append("\\n");
                                curPos++;
                            } else if (c=='\\') {// 单独一个'\'出现为非法
//                                throw new CompilerException("a",lineNum,"invalid Char in FormatString:'"+c+"'"); // fixme:错误处理a
                                ErrorReporter.reportError(lineNum, ErrorType.EA);
                            } else {
                                token.append(c);
                            }
                        } else if (c == '%') { // %d(FormatChar)
                            if ((curPos+1)<source_length && source.charAt(curPos+1) == 'd') {
                                token.append("%d");
                                d++;
                                curPos++;
                            } else {
//                                throw new CompilerException("invalid FormatChar:'"+c+"'"); // fixme:错误处理a
                                ErrorReporter.reportError(lineNum, ErrorType.EA);
                            }
                        } else if (c == '"') {
                            token.append(c);
                            curPos++;
                            break;
                        }
                        else { // 非法Char
//                            throw new CompilerException("a",lineNum,"invalid Char in FormatString:'"+c+"'"); // fixme:错误处理a
                            ErrorReporter.reportError(lineNum, ErrorType.EA);
                        }
                        curPos++;
                    }
                    s = token.toString();
                    token.setLength(0);
                    token_list.add(new Token(s,LexType.STRCON,lineNum,d));
                    break;
                default:
                    throw new CompilerException("invalid input");
            }
        }
    }

    /**
     * 是NormalChar的判断函数
     * @param c 单字符
     * @return boolean
     */
    public static boolean isNormalChar(char c) {
        return c == 32 || c == 33 || (c >= 40 && c <= 126);
    }

    /**
     * 是保留词的判断函数
     *
     * @param word 单词
     * @return boolean
     */
    public static boolean isReserveWord(String word) {
        return reserveWords.containsKey(word);
    }

    public Token next() throws Exception {
        Parser.prev = Parser.now;
        try{
            Parser.now = token_list.get(++curIndex);
            return Parser.now;
        } catch (Exception e){
            Parser.now = new Token();
            return Parser.now;
        }
    }

    public Token preRead() {
        try{
            Parser.pre_read = token_list.get(curIndex+1);
            return Parser.pre_read;
        } catch (Exception e){
            Parser.pre_read = new Token();
            return Parser.pre_read;
        }
    }

    public Token prepreRead() {
        try{
            Parser.prepre_read = token_list.get(curIndex+2);
            return Parser.prepre_read;
        } catch (Exception e){
            Parser.prepre_read = new Token();
            return Parser.prepre_read;
        }
    }

}
