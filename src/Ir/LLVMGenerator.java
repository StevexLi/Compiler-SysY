package Ir;

import DataStructure.Token;
import Ir.types.*;
import Ir.values.*;
import Ir.values.instructions.IROp;
import Lexer.LexType;
import Parser.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static Parser.Stmt.StmtType.BLOCK;

public class LLVMGenerator {
    private static LLVMGenerator instance = new LLVMGenerator();
    public static LLVMGenerator getInstance(){
        return instance;
    }
    private static BuildFactory build_factory = BuildFactory.getInstance();

    private boolean is_global = true;
    private boolean is_const = false;

    private Function cur_func;
    private ArrayList<Value> func_args_list;
    private BasicBlock cur_block;

    private IROp tmp_op;
    private Value tmp_value;
    private IRType tmp_type;
    private Integer save_value_int;
    private IROp save_op;


    /**
     * 栈式符号表及常量表和相应的方法
     */
    private ArrayList<Map<String, Value>> symbol_table = new ArrayList<>();
    private ArrayList<Map<String, Integer>> const_table = new ArrayList<>();
    private void makeSymbolAndConstTable() {
        symbol_table.add(new HashMap<String, Value>());
        const_table.add(new HashMap<>());
    }
    private void popSymbolAndConstTable() {
        symbol_table.remove(symbol_table.size()-1);
        const_table.remove(const_table.size()-1);
    }
    private Map<String, Value> getCurSymbolTable() {
        return symbol_table.get(symbol_table.size()-1);
    }
    private void addSymbol(String name, Value value) {
        getCurSymbolTable().put(name, value);
    }
    private void addGlobalSymbol(String name, Value value) {
        symbol_table.get(0).put(name, value);
    }
    private Value getValue(String name) {
        for (int i= symbol_table.size()-1; i>=0; i--){
            if (symbol_table.get(i).containsKey(name)){
                return symbol_table.get(i).get(name);
            }
        }
        return null;
    }
    private Map<String, Integer> getCurConstTable() {
        return const_table.get(const_table.size()-1);
    }
    private void addConstSymbol(String name, Integer value) {
        getCurConstTable().put(name, value);
    }
    private Integer getConstInt(String name) {
        for (int i= const_table.size()-1; i>=0; i--){
            if (const_table.get(i).containsKey(name)){
                return const_table.get(i).get(name);
            }
        }
        return 0;
    }


    public int calculate(IROp op, int left, int right) {
        switch (op) {
            case Add -> {
                return left + right;
            }
            case Sub -> {
                return left - right;
            }
            case Mul -> {
                return left * right;
            }
            case Div -> {
                return left / right;
            }
            case Mod -> {
                return left % right;
            }
        }
        return 0;
    }



    /**
     * CompUnit → {Decl} {FuncDef} MainFuncDef
     *
     * @param compUnit AST的根节点的data
     */
    public void visitCompUnit(CompUnit compUnit) {
        makeSymbolAndConstTable(); // 最顶层的符号和常量表
        addSymbol("getint", build_factory.buildLibraryFunction("getint", IntegerType.i32, new ArrayList<>()));
        addSymbol("putint", build_factory.buildLibraryFunction("putint", VoidType.voidType, new ArrayList<>(Collections.singletonList(IntegerType.i32)))); // 创造一个不可变列表
        addSymbol("putchar", build_factory.buildLibraryFunction("putchar", VoidType.voidType, new ArrayList<>(Collections.singletonList(IntegerType.i32)))); // 创造一个不可变列表
        addSymbol("putstr", build_factory.buildLibraryFunction("putstr", VoidType.voidType, new ArrayList<>(Collections.singletonList(new PointerType(IntegerType.i8))))); // 创造一个不可变列表


        for (Decl decl : compUnit.getDecl()){
            visitDecl(decl);
        }
//        for (FuncDef funcDef : getFuncDef()){
//            visitFuncDef(funcDef);
//        }
        visitMainFuncDef(compUnit.getMainFuncDef());
    }

    /**
     * Decl → ConstDecl | VarDecl
     */
    public void visitDecl(Decl decl) {
        if (decl.getVarDecl()!=null){
            visitVarDecl(decl.getVarDecl());
        } else {
            visitConstDecl(decl.getConstDecl());
        }
    }

    /**
     * VarDecl → BType VarDef { ',' VarDef } ';'
     */
    public void visitVarDecl(VarDecl varDecl) {
        tmp_type = null;
        switch (varDecl.getBType()){
            case INTTK -> tmp_type = IntegerType.i32;
        }
        for (VarDef def : varDecl.getVarDefList()){
            visitVarDef(def);
        }
    }

    /**
     * VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义
     * | Ident { '[' ConstExp ']' } '=' InitVal
     */
    public void visitVarDef(VarDef varDef) {
        String ident_name = varDef.getIdentName();
        if (varDef.getDim()==0){
            if (varDef.getInitVal()!=null){ // 有初始值
                tmp_value = null;
                if (is_global){
                    is_const = true;
                    save_value_int = null;
                }
                visitInitVal(varDef.getInitVal());
                is_const = false;
            } else { // 无初始值
                tmp_value = null;
                if (is_global){
                    save_value_int = null;
                }
            }
            if (is_global){
                tmp_value = build_factory.buildGlobalVar(ident_name, tmp_type, false, build_factory.getConstInt(save_value_int == null ? 0:save_value_int));
                addSymbol(ident_name, tmp_value);
            } else {
                tmp_value = build_factory.buildVar(cur_block, tmp_value, is_const, tmp_type);
                addSymbol(ident_name, tmp_value);
            }
        } else { // TODO:数组

        }
    }

    /**
     * 变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
     * // 1.表达式初值 2.一维数组初值 3.二维数组初值
     */
    public void visitInitVal(InitVal initVal) {
        if (initVal.getExp()!=null){
            visitExp(initVal.getExp());
        } else { // TODO:数组

        }
    }



    /**
     * ConstDecl → 'const' BType ConstDef { ',' ConstDef }
     */
    public void visitConstDecl(ConstDecl constDecl) {
        tmp_type = null;
        switch (constDecl.getBType()){
            case INTTK -> tmp_type = IntegerType.i32;
        }
        for (ConstDef def : constDecl.getConstDefList()){
            visitConstDef(def);
        }
    }

    /**
     * ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal // 包含普通变量、一维数组、二维数组共三种情况
     */
    public void visitConstDef(ConstDef constDef) {
        String ident_name = constDef.getIdentName();
        if (constDef.getDim()==0) { // 非数组
            visitConstInitVal(constDef.getConstInitVal());
            tmp_value = build_factory.getConstInt(save_value_int == null ? 0:save_value_int);
            addConstSymbol(ident_name, save_value_int);
            if (is_global){
                tmp_value = build_factory.buildGlobalVar(ident_name, tmp_type, true, tmp_value);
                addSymbol(ident_name, tmp_value);
            } else {
                tmp_value = build_factory.buildVar(cur_block, tmp_value, true, tmp_type);
                addSymbol(ident_name, tmp_value);
            }
        } else { // TODO:数组

        }
    }

    /**
     * ConstInitVal → ConstExp
     * | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
     * // 1.常表达式初值 2.一维数组初值 3.二维数组初值
     */
    public void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp()!=null){
            visitConstExp(constInitVal.getConstExp());
        } else { //TODO:数组

        }
    }

    /**
     * ConstExp → AddExp 必须使用符号常量
     */
    public void visitConstExp(ConstExp constExp) {
        is_const = true;
        save_value_int = null;
        visitAddExp(constExp.getAddExp());
        is_const = false;
    }

    /**
     * MainFuncDef → 'int' 'main' '(' ')' Block
     */
    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
        Function func = build_factory.buildFunction("main",IntegerType.i32, new ArrayList<>());
        cur_func = func;
        addSymbol("main",func);
        is_global = false;
        makeSymbolAndConstTable();
        addSymbol("main",func);
        cur_block = build_factory.buildBasicBlock(func);
        func_args_list = build_factory.getFuncArgs(cur_func);
        visitBlock(mainFuncDef.getBlock());
        is_global = true;
        popSymbolAndConstTable();
        build_factory.checkBlockEnd(cur_block);
    }

    /**
     * Block → '{' { BlockItem } '}'
     */
    public void visitBlock(Block block) {
        ArrayList<BlockItem> block_items = block.getBlockItem();
        for (BlockItem blk_item : block_items){
            visitBlockItem(blk_item);
        }
    }

    /**
     * BlockItem → Decl | Stmt
     */
    public void visitBlockItem(BlockItem blk_item) {
        if (blk_item.getStmt()!=null){ // Stmt
            visitStmt(blk_item.getStmt());
        } else { // Decl
            visitDecl(blk_item.getDecl());
        }
    }

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
     */
    public void visitStmt(Stmt stmt) {
        switch (stmt.getStmt_type()){
            case LVALASSIGNEXP:
                if (stmt.getLval().getExps().isEmpty()){ // LVal非数组
                    Value pointer = getValue(stmt.getLval().getIdentString());
                    visitExp(stmt.getExp_single());
                    tmp_value = build_factory.buildStore(cur_block, pointer, tmp_value);
                } else { // TODO:数组

                }
                break;
            case EXP:
                if (stmt.getExp_single()!=null){
                    visitExp(stmt.getExp_single());
                }
                break;
            case BLOCK:
                makeSymbolAndConstTable();
                visitBlock(stmt.getBlock());
                popSymbolAndConstTable();
                break;
            case RETURN:
                if (stmt.getExp_single()!=null){ // 有Exp
                    visitExp(stmt.getExp_single());
//                    Value value = tmp_value;
//                    value.setType(IntegerType.i32);
                    build_factory.buildReturn(cur_block, tmp_value);
                } else { // 无Exp
                    build_factory.buildReturn(cur_block);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Exp → AddExp
     */
    public void visitExp(Exp exp) {
        tmp_value = null;
        save_value_int = null;
        visitAddExp(exp.getAddExp());
    }

    /**
     * AddExp → MulExp | AddExp ('+' | '−') MulExp
     */
    public void visitAddExp(AddExp add_exp) {
        if (is_const){
            Integer value = null; //
            IROp op = null; //
            save_value_int = null;
            ArrayList<Token> mulexp_list =  add_exp.getMulExp_list();
            visitMulExp((MulExp)(mulexp_list.get(0).nt));
            value = save_value_int;
            for (int i=2; i<mulexp_list.size(); i+=2){
                visitMulExp((MulExp)(mulexp_list.get(i).nt));
                op = mulexp_list.get(i-1).type.equals(LexType.PLUS) ? IROp.Add : IROp.Sub;
                value = save_value_int = calculate(op, value, save_value_int);
            }
        } else {
            Value value = null; //
            IROp op = null; //
            tmp_value = null;
            ArrayList<Token> mulexp_list =  add_exp.getMulExp_list();
            visitMulExp((MulExp)(mulexp_list.get(0).nt));
            value = tmp_value;
            for (int i=2; i<mulexp_list.size(); i+=2){
                visitMulExp((MulExp)(mulexp_list.get(i).nt));
                op = mulexp_list.get(i-1).type.equals(LexType.PLUS) ? IROp.Add : IROp.Sub;
                value = tmp_value = build_factory.buildBinary(cur_block, op, value, tmp_value); // TODO: really?
            }
        }
    }

    /**
     * MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     */
    public void visitMulExp(MulExp mul_exp) {
        if (is_const) {
            Integer value = null; //
            IROp op = null; //
            save_value_int = null;
            ArrayList<Token> unaryexp_list =  mul_exp.getUnaryExp_list();
            visitUnaryExp((UnaryExp)(unaryexp_list.get(0).nt));
            value = save_value_int;
            for (int i=2; i<unaryexp_list.size(); i+=2){
                visitUnaryExp((UnaryExp)(unaryexp_list.get(i).nt));
                switch (unaryexp_list.get(i-1).type) {
                    case MULT -> op = IROp.Mul;
                    case DIV -> op = IROp.Div;
                    case MOD -> op = IROp.Mod;
                }
                value = save_value_int = calculate(op, value, save_value_int);
            }
        } else {
            Value value = null; //
            IROp op = null; //
            tmp_value = null;
            ArrayList<Token> unaryexp_list =  mul_exp.getUnaryExp_list();
            visitUnaryExp((UnaryExp)(unaryexp_list.get(0).nt));
            value = tmp_value;
            for (int i=2; i<unaryexp_list.size(); i+=2){
                visitUnaryExp((UnaryExp)(unaryexp_list.get(i).nt));
                switch (unaryexp_list.get(i-1).type) {
                    case MULT -> op = IROp.Mul;
                    case DIV -> op = IROp.Div;
                    case MOD -> op = IROp.Mod;
                }
                value = tmp_value = build_factory.buildBinary(cur_block, op, value, tmp_value); // TODO: really?
            }
        }
    }

    /**
     * UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
     */
    public void visitUnaryExp(UnaryExp unary_exp) {
        if (unary_exp.getPrimaryExp()!=null){ // PrimaryExp
            visitPrimaryExp(unary_exp.getPrimaryExp());
        } else if (unary_exp.getUnaryOp()!=null){
            switch (unary_exp.getUnaryOp().getUnaryOpType()){
                case PLUS -> visitUnaryExp(unary_exp.getUnaryExp());
                case MINU -> {
                    visitUnaryExp(unary_exp.getUnaryExp());
                    if (is_const){
                        save_value_int = -save_value_int;
                    } else {
                        tmp_value = build_factory.buildBinary(cur_block, IROp.Sub, ConstInt.ZERO, tmp_value);
                    }
                }
                //                case NOT ->
            }
        }
        // TODO:函数的情况
    }

    /**
     * PrimaryExp → '(' Exp ')' | LVal | Number
     */
    public void visitPrimaryExp(PrimaryExp primary_exp) {
        if (primary_exp.getNumber()!=null){ // Number
            visitNumber(primary_exp.getNumber());
        } else if (primary_exp.getExp()!=null){ // (Exp)
            visitExp(primary_exp.getExp());
        } else { // LVal
            visitLVal(primary_exp.getLVal());
        }
    }

    /**
     * LVal → Ident {'[' Exp ']'} //1.普通变量 2.一维数组 3.二维数组
     */
    public void visitLVal(LVal lVal) {
        if (is_const){ // 定义时使用
            StringBuilder sb = new StringBuilder(lVal.getIdentString());
            if (!lVal.getExps().isEmpty()){
                sb.append("0;"); // 数组每一个元素在符号表中有一个名字
                for (Exp exp : lVal.getExps()){
                    visitExp(exp);
                    sb.append(build_factory.getConstInt(save_value_int == null ? 0:save_value_int).getValue()).append(";");
                }
            }
            save_value_int = getConstInt(sb.toString());
        } else {
            if (lVal.getExps().isEmpty()){
                Value l = getValue(lVal.getIdentString());
                tmp_value = l;
                IRType type = l.getType();
                if (!(((PointerType) type).getTargetType() instanceof ArrayType)){
                    tmp_value = build_factory.buildLoad(cur_block, tmp_value);
                } else { // TODO:数组

                }
            } else { // TODO:数组

            }
        }
    }

    public void visitNumber(_Number_ number){
        if (is_const){
            save_value_int = number.getValue();
        } else {
            tmp_value = build_factory.getConstInt(number.getValue());
        }
    }

}
