package DataStructure;

import Exception.*;

/**
 * 符号枚举类
 *
 * @author Stevex
 * @date 2023/11/07
 */
public enum SymbolType {
    RETINT("retint","retint"),
    RETVOID("retvoid","retvoid"),
    VAR("var","var"),
    DIM1ARRAY("dim1array","dim1array"),
    DIM2ARRAY("dim2array","dim2array"),
    FUNC("func","func"),
    MAINFUNC("mainfunc","mainfunc"),
    ;

    private String word;
    private String type;

    /**
     * 符号枚举类构造方法
     *
     * @param word 词
     * @param type 类型
     */
    SymbolType(String word, String type) {
        this.word = word;
        this.type = type;
    }

    /**
     * 获取枚举类word
     *
     * @return {@link String}
     */
    public String stEnumGetWord() {
        return word;
    }

    /**
     * 获取枚举类type
     *
     * @return {@link String}
     */
    public String stEnumGetType() {
        return type;
    }

    /**
     * 按word获取符号枚举类类型type
     *
     * @param word 单词
     * @return {@link String}
     * @throws CompilerException 编译器异常
     */
    public static String stEnumGetTypeByWord(String word) throws CompilerException {
        String type = "!NONE!";
        SymbolType[] symbolTypes = SymbolType.values();
        for (SymbolType symType : symbolTypes) {
            if(symType.stEnumGetWord().equals(word)){
                type =  symType.stEnumGetType();
                break;
            }
        }
        if (type.equals("!NONE!")){
            throw new CompilerException("invalid word in SymbolType");
        }
        return type;
    }

    /**
     * 按type获取符号枚举类类型word
     *
     * @param type 类型
     * @return {@link String}
     * @throws CompilerException 编译器异常
     */
    public static String stEnumGetWordByType(String type) throws CompilerException {
        String word = "!NONE!";
            SymbolType[] symbolTypes = SymbolType.values();
        for (SymbolType symType : symbolTypes) {
            if(symType.stEnumGetType().equals(type)){
                word = symType.stEnumGetWord();
                break;
            }
        }
        if (word.equals("!NONE!")){
            throw new CompilerException("invalid type in SymbolType");
        }
        return word;
    }
}
