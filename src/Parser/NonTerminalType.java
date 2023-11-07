package Parser;

import Exception.*;


/**
 * 非终结符类型枚举类
 *
 * @author Stevex
 * @date 2023/10/07
 */
public enum NonTerminalType {
    COMPUNIT("<CompUnit>","CompUnit"),
    DECL("<Decl>","Decl"),
    FUNCDEF("<FuncDef>","FuncDef"),
    MAINFUNCDEF("<MainFuncDef>","MainFuncDef"),
    CONSTDECL("<ConstDecl>","ConstDecl"),
    VARDECL("<VarDecl>","VarDecl"),
    BTYPE("<BType>","BType"),
    CONSTDEF("<ConstDef>","ConstDef"),
    CONSTEXP("<ConstExp>","ConstExp"),
    CONSTINITVAL("<ConstInitVal>","ConstInitVal"),
    ADDEXP("<AddExp>","AddExp"),
    MULEXP("<MulExp>","MulExp"),
    UNARYEXP("<UnaryExp>","UnaryExp"),
    UNARYOP("<UnaryOp>","UnaryOp"),
    PRIMARYEXP("<PrimaryExp>","PrimaryExp"),
    FUNCRPARAMS("<FuncRParams>","FuncRParams"),
    EXP("<Exp>","Exp"),
    LVAL("<LVal>","LVal"),
    NUMBER("<Number>","Number"),
    VARDEF("<VarDef>","VarDef"),
    INITVAL("<InitVal>","InitVal"),
    FUNCTYPE("<FuncType>","FuncType"),
    FUNCFPARAMS("<FuncFParams>","FuncFParams"),
    FUNCFPARAM("<FuncFParam>","FuncFParam"),
    BLOCK("<Block>","Block"),
    BLOCKITEM("<BlockItem>","BlockItem"),
    STMT("<Stmt>","Stmt"),
    FORSTMT("<ForStmt>","ForStmt"),
    COND("<Cond>","Cond"),
    LOREXP("<LOrExp>","LOrExp"),
    LANDEXP("<LAndExp>","LAndExp"),
    EQEXP("<EqExp>","EqExp"),
    RELEXP("<RelExp>","RelExp"),
    ;

    private String word;
    private String type;

    /**
     * 非终结符枚举类构造方法
     *
     * @param word 词
     * @param type 类型
     */
    NonTerminalType(String word, String type) {
        this.word = word;
        this.type = type;
    }

    /**
     * 获取枚举类word
     *
     * @return {@link String}
     */
    public String ntEnumGetWord() {
        return word;
    }

    /**
     * 获取枚举类type
     *
     * @return {@link String}
     */
    public String ntEnumGetType() {
        return type;
    }

    /**
     * 按word获取单词枚举类类型type
     *
     * @param word 单词
     * @return {@link String}
     * @throws CompilerException 编译器异常
     */
    public static String ntEnumGetTypeByWord(String word) throws CompilerException {
        String type = "!NONE!";
        NonTerminalType[] nonTerminalTypes = NonTerminalType.values();
        for (NonTerminalType lexType : nonTerminalTypes) {
            if(lexType.ntEnumGetWord().equals(word)){
                type =  lexType.ntEnumGetType();
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
    public static String ntEnumGetWordByType(String type) throws CompilerException {
        String word = "!NONE!";
        NonTerminalType[] nonTerminalTypes = NonTerminalType.values();
        for (NonTerminalType lexType : nonTerminalTypes) {
            if(lexType.ntEnumGetType().equals(type)){
                word = lexType.ntEnumGetWord();
                break;
            }
        }
        if (word.equals("!NONE!")){
            throw new CompilerException("invalid type");
        }
        return word;
    }
}
