package DataStructure;

import Exception.*;

/**
 * 错误类型枚举类
 *
 * @author Stevex
 * @date 2023/11/07
 */
public enum ErrorType {
    EA("a","非法符号"), //
    EB("b","名字重定义"),
    EC("c","未定义的名字"),
    ED("d","函数参数个数不匹配"),
    EE("e","函数参数类型不匹配"),
    EF("f","无返回值的函数存在不匹配的return语句"),
    EG("g","有返回值的函数缺少return语句"),
    EH("h","不能改变常量的值"),
    EI("i","缺少分号"),
    EJ("j","缺少右小括号’)’"),
    EK("k","缺少右中括号’]’"),
    EL("l","printf中格式字符与表达式个数不匹配"), //
    EM("m","在非循环块中使用break和continue语句"),
    ;

    private String word;
    private String type;

    /**
     * 符号枚举类构造方法
     *
     * @param word 词
     * @param type 类型
     */
    ErrorType(String word, String type) {
        this.word = word;
        this.type = type;
    }

    /**
     * 获取枚举类word
     *
     * @return {@link String}
     */
    public String erEnumGetWord() {
        return word;
    }

    /**
     * 获取枚举类type
     *
     * @return {@link String}
     */
    public String erEnumGetType() {
        return type;
    }

    /**
     * 按word获取错误枚举类类型type
     *
     * @param word 单词
     * @return {@link String}
     * @throws CompilerException 编译器异常
     */
    public static String erEnumGetTypeByWord(String word) throws CompilerException {
        String type = "!NONE!";
        ErrorType[] errorTypes = ErrorType.values();
        for (ErrorType errorType : errorTypes) {
            if(errorType.erEnumGetWord().equals(word)){
                type =  errorType.erEnumGetType();
                break;
            }
        }
        if (type.equals("!NONE!")){
            throw new CompilerException("invalid word in ErrorType");
        }
        return type;
    }

    /**
     * 按type获取错误枚举类类型word
     *
     * @param type 类型
     * @return {@link String}
     * @throws CompilerException 编译器异常
     */
    public static String erEnumGetWordByType(String type) throws CompilerException {
        String word = "!NONE!";
        ErrorType[] errorTypes = ErrorType.values();
        for (ErrorType errorType : errorTypes) {
            if(errorType.erEnumGetType().equals(type)){
                word = errorType.erEnumGetWord();
                break;
            }
        }
        if (word.equals("!NONE!")){
            throw new CompilerException("invalid type in ErrorType");
        }
        return word;
    }
}
