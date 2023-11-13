package DataStructure;

import Lexer.LexType;
import Parser.NonTerminal;
import Parser.Parser;
import Parser.FuncFParam;

import java.util.ArrayList;

/**
 * 符号类
 *
 * @author Stevex
 * @date 2023/11/07
 */
public class Symbol {
    public static int total_symbol = 0;
    /**
     * 符号编号
     */
    public int id;
    /**
     * 所属符号表ID
     */
    public int table_id;
    /**
     * 符号名
     */
    public String token_string;
    /**
     * 符号对应token
     */
    public ASTNode token_node;
    /**
     * 符号类型（普通变量，一维数组，二维数组，函数）
     */
    public SymbolType type;
    /**
     * 值类型（int）
     */
    public LexType value_type;
    /**
     * 是否为常量
     */
    public boolean is_const;
    /**
     * 若是普通变量或数组，变量值
     */
    public ArrayList<Integer> value;
    public int dim1 = 0;
    public int dim2 = 0;
    public ASTNode dim1_exp;
    public ASTNode dim2_exp;
    /**
     * 若是普通变量或数组，存储值的表达式列表
     */
    public ASTNode value_exp;
    /**
     * 分配寄存器号（待使用）
     */
    public int reg;

    /**
     * 若是函数，返回值类型（INT,VOID）
     */
    public SymbolType ret_type;
    /**
     * 若是函数，参数个数
     */
    public int param_num;
    public boolean is_fparam = false;
    /**
     * 若是函数，参数类型列表
     */
    public ArrayList<SymbolType>param_type_list = new ArrayList<SymbolType>();
    public ArrayList<ASTNode> param_exp_list;

    /**
     * 普通变量、数组变量声明
     *
     * @param token_node ASTNode类型Ident节点
     * @param symbolType 符号类型
     * @param is_const   是常量
     * @param value_type 值类型
     * @param has_value  有值与否
     * @param dim1       ASTNode类一维大小表达式
     * @param dim2       ASTNode类二维大小表达式
     * @param value_exp  ASTNode类型InitVal/ConstInitVal值表达式
     */
    public Symbol(ASTNode token_node, SymbolType symbolType, boolean is_const, LexType value_type, boolean has_value, ASTNode dim1, ASTNode dim2, ASTNode value_exp) {
        this.id = ++Symbol.total_symbol;
        this.table_id = Parser.cur.getTableId();
        this.token_node = token_node;
        this.token_string = ((Token)this.token_node.getData()).token;
        this.type = symbolType;
        this.is_const = is_const;
        this.value_type = value_type;
        this.dim1_exp = dim1;
        this.dim2_exp = dim2;
        if (has_value) {
            this.value_exp = value_exp;
        }
        // TODO: TRANSLATE DIM AND EXP INTO REAL VALUES!
    }

    /**
     * 函数形参定义
     *
     * @param token_node ASTNode类型Ident节点
     * @param symbolType 符号类型
     * @param value_type 值类型
     * @param dim2       DIM2
     */
    public Symbol(ASTNode token_node, SymbolType symbolType, LexType value_type, ASTNode dim2) {
        this.id = ++Symbol.total_symbol;
        this.table_id = Parser.cur.getTableId();
        this.token_node = token_node;
        this.token_string = ((Token)this.token_node.getData()).token;
        this.type = symbolType;
//        this.is_const = is_const;
        this.is_fparam = true;
        this.value_type = value_type;
//        this.dim1_exp = dim1;
        this.dim2_exp = dim2;
        // TODO: TRANSLATE DIM INTO REAL VALUES!
    }

    /**
     * 函数符号声明
     *
     * @param token_node  ASTNode类型Ident节点
     * @param symbolType  符号类型
     * @param retType     返回值类型
     * @param paramNum    参数个数
     * @param FuncFParams 形参列表
     */
    public Symbol(ASTNode token_node, SymbolType symbolType, SymbolType retType, int paramNum, ArrayList<ASTNode> FuncFParams) {
        this.id = ++Symbol.total_symbol;
        this.table_id = Parser.cur.getTableId();
        this.token_node = token_node;
        this.token_string = ((Token)this.token_node.getData()).token;
        this.type = symbolType;
        this.ret_type = retType;
        this.param_num = paramNum;
        this.param_exp_list = FuncFParams;
        for (ASTNode param : this.param_exp_list){
            int dim = ((FuncFParam)(((Token)param.getData()).nt)).getDim();
            switch (dim){
                case 0: // 普通变量
                    this.param_type_list.add(SymbolType.VAR);
                    break;
                case 1: // 一维数组
                    this.param_type_list.add(SymbolType.DIM1ARRAY);
                    break;
                case 2: // 二维数组
                    this.param_type_list.add(SymbolType.DIM2ARRAY);
                    break;
            }
        }
    }

    /**
     * 获取变量维度
     * 若非变量返回-1
     *
     * @return int
     */
    public int getDimension() {
        if (type.equals(SymbolType.VAR))
            return 0;
        else if (type.equals(SymbolType.DIM1ARRAY))
            return 1;
        else if (type.equals(SymbolType.DIM2ARRAY))
            return 2;
        else
            return -1;
    }

    /**
     * 获取值列表
     *
     * @return {@link ArrayList}<{@link Integer}>
     */
    public ArrayList<Integer> getValueList() {
        return value;
    }

    /**
     * 获取指定位置的值
     *
     * @param index 位置
     * @return int
     */
    public int getValue(int index) {
        // TODO:越界访问！！！
        return value.get(index);
    }

    /**
     * 获取普通变量的值
     *
     * @return int
     */
    public int getValue() {
        return value.get(0);
    }

    /**
     * 设置值列表（数组）
     *
     * @param arrayList 数组列表
     */
    public void setValueList(ArrayList<Integer> arrayList) {
        this.value = arrayList;
    }

    /**
     * 设定数组指定位置的值
     *
     * @param pos   指定位置
     * @param value 值
     */
    public void setValue(int pos, int value) {
        if (this.value.size()-1<pos)
            this.value.add(value);
        else
            this.value.set(pos,value);
    }

    /**
     * 设定值（普通变量）
     *
     * @param value 普通变量的值
     */
    public void setValue(int value) {
        if (this.value.size()==1)
            this.value.add(0,value);
        else {
            this.value.clear();
            this.value.add(value);
        }
    }

}
