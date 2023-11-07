package DataStructure;

import java.util.ArrayList;

/**
 * 符号类
 *
 * @author Stevex
 * @date 2023/11/07
 */
public class Symbol {
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
    public Token token;
    /**
     * 符号类型（普通变量，一维数组，二维数组，函数）
     */
    public SymbolType type;
    /**
     * 是否为常量
     */
    public boolean _const_;
    /**
     * 若是普通变量或数组，变量值
     */
    public ArrayList<Integer> value;
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
    /**
     * 若是函数，参数类型列表
     */
    public ArrayList<SymbolType>param_type_list = new ArrayList<SymbolType>();

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
     * 设定指定位置的值
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
