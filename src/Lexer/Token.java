package Lexer;

import Parser.ASTNode;
import Parser.NonTerminal;
import Parser.NonTerminalType;

/**
 * 单词类
 *
 * @author Stevex
 * @date 2023/09/22
 */
public class Token {
    /**
     * 单词字符串
     */
    public String token;
    /**
     * 单词类型
     */
    public LexType type;
    /**
     * 单词所在行
     */
    public int line;
    /**
     * 单词数值（仅数值常量）
     */
    public int value;
    /**
     * 非终结符类型
     */
    public NonTerminal nt;

    /**
     * 第一个孩子(建立树时使用）
     */
    public ASTNode firstchild;
    /**
     * 下一个兄弟姐妹（建立树时使用）
     */
    public ASTNode nextsibling;

    /**
     * 判断常量
     */
    public boolean is_const = false;

    public boolean is_end = false;


    /**
     * Token构造方法
     *
     * @param token 单词
     * @param type  单词类型
     * @param line  单词所在行
     */
    Token(String token, LexType type, int line) {
        this.token = token;
        this.type = type;
        this.line = line;
    }

    /**
     * Token构造方法
     *
     * @param token 单词
     * @param type  单词类型
     * @param line  单词所在行
     * @param value 单词数值
     */
    Token(String token, LexType type, int line, int value) {
        this.token = token;
        this.type = type;
        this.line = line;
        this.value = value;
    }

    /**
     * Token构造方法
     *
     * @param nt 非终结符类型
     */
    public Token(NonTerminal nt) throws Exception{
        this.nt = nt;
        this.firstchild = nt.firstchild;
        this.nextsibling = nt.nextsibling;
    }

    /**
     * Token构造方法，用于表示文档到达结尾
     */
    Token() {
        is_end = true;
        line = -1;
    }

    /**
     * 单词枚举类型是否与传入类型一致
     *
     * @param lexType 单词类型枚举类
     * @return {@link Boolean}
     */
    public Boolean equalLexType(LexType lexType) {
        if (this.type==null)
            return false;
        return this.type.equals(lexType);
    }

    public Boolean equalNonTerminalType(NonTerminalType nonTerminalType) {
        if (this.nt==null)
            return false;
        return this.nt.nt_type.equals(nonTerminalType);
    }

    /**
     * 是否为Ident
     *
     * @return boolean
     */
    public boolean isIdent() {
        if (this.type==null)
            return false;
        return this.type.equals(LexType.IDENFR);
    }

    /**
     * 是常量标识符
     *
     * @return boolean
     */
    public boolean isConst() {
        return this.is_const;
    }

    /**
     * 是数值常量（数字）
     *
     * @return boolean
     */
    public boolean isIntConst() {
        if (this.type==null)
            return false;
        return this.type.equals(LexType.INTCON);
    }

    public boolean isStrCon() {
        if (this.type==null)
            return false;
        return this.type.equals(LexType.STRCON);
    }

    /**
     * 重写print方法
     *
     * @return {@link String}
     */
    @Override
    public String toString() {
        if (nt!=null) {
            return (nt.toString());
        }
        if (!type.lexEnumGetType().equals("INTCON")){
            return (token+' '+type+' '+line);
        }
        else {
            return (token+' '+type+' '+line+' '+value);
        }
    }
}
