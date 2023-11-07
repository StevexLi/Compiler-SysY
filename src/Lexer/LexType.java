package Lexer;

import Exception.*;

import java.util.HashMap;

/**
 * 单词类型枚举类
 *
 * @author Stevex
 * @date 2023/09/22
 */
public enum LexType {
    IDENFR("Ident","IDENFR"),
    INTCON("IntConst","INTCON"),
    STRCON("FormatString", "STRCON"),
    MAINTK("main", "MAINTK"),
    CONSTTK("const", "CONSTTK"),
    INTTK("int", "INTTK"),
    BREAKTK("break", "BREAKTK"),
    CONTINUETK("continue", "CONTINUETK"),
    IFTK("if", "IFTK"),
    ELSETK("else", "ELSETK"),
    NOT("!", "NOT"),
    AND("&&", "AND"),
    OR("||", "OR"),
    FORTK("for", "FORTK"),
    GETINTTK("getint", "GETINTTK"),
    PRINTFTK("printf", "PRINTFTK"),
    RETURNTK("return", "RETURNTK"),
    PLUS("+", "PLUS"),
    MINU("-", "MINU"),
    VOIDTK("void", "VOIDTK"),
    MULT("*", "MULT"),
    DIV("/", "DIV"),
    MOD("%", "MOD"),
    LSS("<", "LSS"),
    LEQ("<=", "LEQ"),
    GRE(">", "GRE"),
    GEQ(">=", "GEQ"),
    EQL("==", "EQL"),
    NEQ("!=", "NEQ"),
    ASSIGN("=", "ASSIGN"),
    SEMICN(";", "SEMICN"),
    COMMA(",", "COMMA"),
    LPARENT("(", "LPARENT"),
    RPARENT(")", "RPARENT"),
    LBRACK("[", "LBRACK"),
    RBRACK("]", "RBRACK"),
    LBRACE("{", "LBRACE"),
    RBRACE("}", "RBRACE"),
    ;

    private String word;
    private String type;

    /**
     * 单词枚举类构造方法
     *
     * @param word 词
     * @param type 类型
     */
    LexType(String word, String type) {
        this.word = word;
        this.type = type;
    }

    /**
     * 获取枚举类word
     *
     * @return {@link String}
     */
    public String lexEnumGetWord() {
        return word;
    }

    /**
     * 获取枚举类type
     *
     * @return {@link String}
     */
    public String lexEnumGetType() {
        return type;
    }

    /**
     * 建立保留词哈希表
     *
     * @param reserveWords 保留词
     */
    static void buildReserveWordsMap(HashMap<String, LexType> reserveWords) {
        LexType[] lexTypes = LexType.values();
        for (LexType lexType : lexTypes) {
            if(lexType.lexEnumGetType().matches(".*TK$")){
                reserveWords.put(lexType.lexEnumGetWord(),lexType);
            }
        }
    }

    /**
     * 按word获取单词枚举类类型type
     *
     * @param word 单词
     * @return {@link String}
     * @throws CompilerException 编译器异常
     */
    public static String lexEnumGetTypeByWord(String word) throws CompilerException {
        String type = "!NONE!";
        LexType[] lexTypes = LexType.values();
        for (LexType lexType : lexTypes) {
            if(lexType.lexEnumGetWord().equals(word)){
                type =  lexType.lexEnumGetType();
                break;
            }
        }
        if (type.equals("!NONE!")){
            throw new CompilerException("invalid word");
        }
        return type;
    }

    /**
     * 按type获取单词枚举类类型word
     *
     * @param type 类型
     * @return {@link String}
     * @throws CompilerException 编译器异常
     */
    public static String lexEnumGetWordByType(String type) throws CompilerException {
        String word = "!NONE!";
        LexType[] lexTypes = LexType.values();
        for (LexType lexType : lexTypes) {
            if(lexType.lexEnumGetType().equals(type)){
                word = lexType.lexEnumGetWord();
                break;
            }
        }
        if (word.equals("!NONE!")){
            throw new CompilerException("invalid type");
        }
        return word;
    }
}
