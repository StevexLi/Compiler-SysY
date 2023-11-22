package Ir.values.instructions;

public enum IROp {
    // 二元运算符
    Add,
    Sub,
    Mul,
    Div,
    Mod,
    Shl,
    Shr,
    And,
    Or,

    // 关系运算符
    Lt,
    Le,
    Ge,
    Gt,
    Eq,
    Ne,

    // 类型转换
    Zext,
    Bitcast,
    // 内存操作
    Alloca,
    Load,
    Store,
    GEP,

    // Phi 指令
    Phi,
    MemPhi,
    LoadDep,
    // 跳转指令
    Br,
    Call,
    Ret,

    // !运算符
    Not
}
