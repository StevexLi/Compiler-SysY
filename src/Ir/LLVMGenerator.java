package Ir;

import DataStructure.Token;
import Ir.types.*;
import Ir.values.*;
import Ir.values.instructions.ConstArray;
import Ir.values.instructions.IROp;
import Lexer.LexType;
import Parser.*;

import java.lang.reflect.Type;
import java.util.*;

public class LLVMGenerator {
    private static LLVMGenerator instance = new LLVMGenerator();
    public static LLVMGenerator getInstance(){
        return instance;
    }
    private static BuildFactory build_factory = BuildFactory.getInstance();
    private boolean char_to_string = false;

    private boolean is_global = true;
    private boolean is_const = false;

    private Function cur_func;
    private boolean is_reg = false;
    private ArrayList<IRType> func_type_list;
    private ArrayList<Value> func_args_list;
    private ArrayList<Value> func_rparam_list;
    private int tmp_index = 0;
    private BasicBlock cur_block;
    private BasicBlock cur_true_block;
    private BasicBlock cur_false_block;
    private BasicBlock cur_loop_final_block;
    private BasicBlock continue_block;

    private IROp tmp_op;
    private Value tmp_value;
    private IRType tmp_type;
    private Integer save_value_int;
    private IROp save_op;

    private Value cur_array;
    private ArrayList<Integer> tmp_dims;
    private boolean is_array = false;
    private String tmp_name;
    private int tmp_depth = 0;
    private int tmp_offset = 0;



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
        addSymbol("putch", build_factory.buildLibraryFunction("putch", VoidType.voidType, new ArrayList<>(Collections.singletonList(IntegerType.i32)))); // 创造一个不可变列表
        addSymbol("putstr", build_factory.buildLibraryFunction("putstr", VoidType.voidType, new ArrayList<>(Collections.singletonList(new PointerType(IntegerType.i8))))); // 创造一个不可变列表


        for (Decl decl : compUnit.getDecl()){
            visitDecl(decl);
        }
        for (FuncDef func_def : compUnit.getFuncDef()){
            visitFuncDef(func_def);
        }
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
        if (varDef.getDim()==0){ // 非数组
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
        } else { // 数组
            is_const = true;
            ArrayList<Integer> dim_size = new ArrayList<>();
            for (ConstExp e : varDef.getConst_exp_list()){ // 将维数计算出来
                visitConstExp(e);
                dim_size.add(save_value_int);
            }
            is_const = false;
            tmp_dims = new ArrayList<>(dim_size);
            IRType type = null;
            for (int i=dim_size.size()-1;i>=0;i--){ // 从后往前一层层包裹
                if (type==null){
                    type = build_factory.getArrayType(tmp_type,dim_size.get(i));
                } else {
                    type = build_factory.getArrayType(type, dim_size.get(i));
                }
            }
            if (is_global){
                tmp_value = build_factory.buildGlobalArray(ident_name, type, false);
                if (varDef.getInitVal()!=null){
                    ((ConstArray) ((GlobalVar) tmp_value).getValue()).setInit(true);
                }
            } else {
                tmp_value = build_factory.buildArray(cur_block, type, false);
            }
            addSymbol(ident_name, tmp_value);
            cur_array = tmp_value;
            if (varDef.getInitVal() != null){
                is_array = true;
                tmp_name = ident_name;
                tmp_depth = 0;
                tmp_offset = 0;
                visitInitVal(varDef.getInitVal());
                is_array = false;
            }
            is_const = false;
        }
    }

    /**
     * 变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
     * // 1.表达式初值 2.一维数组初值 3.二维数组初值
     */
    public void visitInitVal(InitVal initVal) {
        if (initVal.getExp()!=null && !is_array){
            visitExp(initVal.getExp());
        } else { // 数组
            if (initVal.getExp() != null){
                if (is_global){
                    is_const = true;
                }
                save_value_int = null;
                tmp_value = null;
                visitExp(initVal.getExp());
                is_const = false;
                tmp_depth = 1;
                if (is_global){
                    tmp_value = build_factory.getConstInt(save_value_int);
                    build_factory.buildInitArray(cur_array, tmp_offset, tmp_value);
                } else {
                    build_factory.buildStore(cur_block, build_factory.buildGEP(cur_block, cur_array, tmp_offset), tmp_value);
                }
                tmp_offset++;
            } else if (!initVal.getInitVal_list().isEmpty()) { // 与ConstInitVal基本类似
                int depth = 0, offset = tmp_offset;
                for (InitVal initval : initVal.getInitVal_list()){
                    visitInitVal(initval);
                    depth = Math.max(depth, tmp_depth);
                }
                depth++;
                int size = 1;
                for (int i=1; i<depth; i++){
                    size *= tmp_dims.get(tmp_dims.size()-i);
                }
                tmp_offset = Math.max(tmp_offset, offset+size);
                tmp_depth = depth;
            }
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
        } else { // 数组
            is_const = true;
            ArrayList<Integer> dim_size = new ArrayList<>();
            for (ConstExp e : constDef.getConst_exp_list()){ // 将维数计算出来
                visitConstExp(e);
                dim_size.add(save_value_int);
            }
            tmp_dims = new ArrayList<>(dim_size);
            IRType type = null;
            for (int i=dim_size.size()-1;i>=0;i--){ // 从后往前一层层包裹
                if (type==null){
                    type = build_factory.getArrayType(tmp_type,dim_size.get(i));
                } else {
                    type = build_factory.getArrayType(type, dim_size.get(i));
                }
            }
            if (is_global){
                tmp_value = build_factory.buildGlobalArray(ident_name, type, true);
                ((ConstArray) ((GlobalVar) tmp_value).getValue()).setInit(true);
            } else {
                tmp_value = build_factory.buildArray(cur_block, type, true);
            }
            addSymbol(ident_name, tmp_value);
            cur_array = tmp_value;
            is_array = true;
            tmp_name = ident_name;
            tmp_depth = 0;
            tmp_offset = 0;
            visitConstInitVal(constDef.getConstInitVal());
            is_array = false;
        }
    }

    /**
     * ConstInitVal → ConstExp
     * | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
     * // 1.常表达式初值 2.一维数组初值 3.二维数组初值
     */
    public void visitConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal.getConstExp()!=null && !is_array){
            visitConstExp(constInitVal.getConstExp());
        } else { // 数组
            if (constInitVal.getConstExp()!=null){
                tmp_value = null;
                visitConstExp(constInitVal.getConstExp());
                tmp_depth = 1;
                tmp_value = build_factory.getConstInt(save_value_int);
                if (is_global){
                    build_factory.buildInitArray(cur_array, tmp_offset, tmp_value);
                } else {
                    build_factory.buildStore(cur_block, build_factory.buildGEP(cur_block, cur_array, tmp_offset), tmp_value);
                }
                StringBuilder name = new StringBuilder(tmp_name);
                ArrayList<Value> values = ((ArrayType)((PointerType)cur_array.getType()).getTargetType()).offset2Index(tmp_offset);
                for (Value v : values){
                    name.append(((ConstInt) v).getValue()).append(";");
                }
                addConstSymbol(name.toString(), save_value_int);
                tmp_offset++;
            } else if (!constInitVal.getConstInitVal_list().isEmpty()){
                int depth = 0;
                int offset = tmp_offset;
                for (ConstInitVal initval : constInitVal.getConstInitVal_list()){
                    visitConstInitVal(initval);
                    depth = Math.max(depth, tmp_depth);
                }
                depth++;
                int size = 1;
                for (int i=1; i<depth; i++){
                    size *= tmp_dims.get(tmp_dims.size()-i);
                }
                tmp_offset = Math.max(tmp_offset, offset+size);
                tmp_depth = depth;
            }
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
     * FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
     */
    public void visitFuncDef(FuncDef funcDef) {
        IRType func_type = VoidType.voidType;
        if (funcDef.getFuncType().equals(LexType.INTTK)) {
            func_type = IntegerType.i32;
        }
        String func_name = funcDef.getIdentString();
        is_global  = false;
        func_type_list = new ArrayList<>();
        if (funcDef.getFuncFParams()!=null){
            visitFuncFParams(funcDef.getFuncFParams());
        }
        Function func = build_factory.buildFunction(func_name, func_type, func_type_list);
        cur_func = func;
        addSymbol(func_name, func);
        makeSymbolAndConstTable();
        addSymbol(func_name, func);
        cur_block = build_factory.buildBasicBlock(func);
        func_args_list = build_factory.getFuncArgs(cur_func);
        is_reg = true;
        if (funcDef.getFuncFParams()!=null) {
            visitFuncFParams(funcDef.getFuncFParams());
        }
        is_reg = false;
        visitBlock(funcDef.getBlock());
        is_global = true;
        popSymbolAndConstTable();
        build_factory.checkBlockEnd(cur_block);
    }

    /**
     * FuncFParams -> FuncFParam { ',' FuncFParam }
     */
    public void visitFuncFParams(FuncFParams funcFParams) {
        if (is_reg) { // 函数对应的符号表内
            tmp_index = 0;
            for (FuncFParam funcFParam : funcFParams.getFuncFParam_list()){
                visitFuncFParam(funcFParam);
                tmp_index++;
            }
        } else {
            func_type_list = new ArrayList<>();
            for (FuncFParam funcFParam : funcFParams.getFuncFParam_list()){
                visitFuncFParam(funcFParam);
                func_type_list.add(tmp_type);
            }
        }
    }

    /**
     * FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
     */
    public void visitFuncFParam(FuncFParam funcFParam) {
        if (is_reg) {
            int i = tmp_index;
            Value value = build_factory.buildVar(cur_block, func_args_list.get(i), false, func_args_list.get(i).getType());
            addSymbol(funcFParam.getIdentString(), value);
        } else {
            if (funcFParam.getDim()==0){ // 非数组
                tmp_type = VoidType.voidType;
                switch (funcFParam.getType()){
                    case INTTK -> tmp_type = IntegerType.i32;
                }
            } else { // 数组
                ArrayList<Integer> dims = new ArrayList<>();
                dims.add(-1);
                if (!funcFParam.getConstExp_list().isEmpty()){
                    for (ConstExp const_exp : funcFParam.getConstExp_list()){
                        is_const = true;
                        visitConstExp(const_exp);
                        dims.add(save_value_int);
                        is_const = false;
                    }
                }
                tmp_type = null;
//                System.out.println(dims);
                for (int i= dims.size()-1; i>=0; i--){
                    if (tmp_type == null){
                        switch (funcFParam.getType()){
                            case INTTK -> tmp_type = IntegerType.i32;
                        }
                    }
                    tmp_type = build_factory.getArrayType(tmp_type, dims.get(i));
                }
            }

        }
    }

    /**
     * MainFuncDef → 'int' 'main' '(' ')' Block
     */
    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
        if (mainFuncDef==null){
            return;
        }
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
     * ForStmt → LVal '=' Exp
     */
    public void visitForStmt(ForStmt stmt){ // 与Stmt中LVal = Exp 一致
        if (stmt.getLVal().getExps().isEmpty()){ // LVal非数组
            Value pointer = getValue(stmt.getLVal().getIdentString());
            visitExp(stmt.getExp_single());
            tmp_value = build_factory.buildStore(cur_block, pointer, tmp_value);
        } else { // 数组
            ArrayList<Value> index_list = new ArrayList<>();
            for (Exp exp : stmt.getLVal().getExps()){
                visitExp(exp);
                index_list.add(tmp_value);
            }
            tmp_value = getValue(stmt.getLVal().getIdentString());
            IRType type = tmp_value.getType();
            IRType target_type = ((PointerType) type).getTargetType();
            if (target_type instanceof PointerType){ // 形如 a[][1]
                tmp_value = build_factory.buildLoad(cur_block, tmp_value);
            } else { // 形如 a[1][2]
                index_list.add(0, ConstInt.ZERO);
            }
            Value addr = build_factory.buildGEP(cur_block, tmp_value, index_list);
            visitExp(stmt.getExp_single());
            tmp_value = build_factory.buildStore(cur_block, addr, tmp_value);
        }
    }

    /**
     * 语句 Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
     * | [Exp] ';' //有无Exp两种情况
     * | Block
     * | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
     * | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
     * | 'break' ';'
     * | 'continue' ';'
     * | 'return' [Exp] ';' // 1.有Exp 2.无Exp
     * | LVal '=' 'getint''('')'';'
     * | 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
     */
    public void visitStmt(Stmt stmt) {
        switch (stmt.getStmt_type()){
            case LVALASSIGNEXP:
                if (stmt.getLVal().getExps().isEmpty()){ // LVal非数组
                    Value pointer = getValue(stmt.getLVal().getIdentString());
                    visitExp(stmt.getExp_single());
                    tmp_value = build_factory.buildStore(cur_block, pointer, tmp_value);
                } else { // 数组
                    ArrayList<Value> index_list = new ArrayList<>();
                    for (Exp exp : stmt.getLVal().getExps()){
                        visitExp(exp);
                        index_list.add(tmp_value);
                    }
                    tmp_value = getValue(stmt.getLVal().getIdentString());
                    IRType type = tmp_value.getType();
                    IRType target_type = ((PointerType) type).getTargetType();
                    if (target_type instanceof PointerType){ // 形如 a[][1]
                        tmp_value = build_factory.buildLoad(cur_block, tmp_value);
                    } else { // 形如 a[1][2]
                        index_list.add(0, ConstInt.ZERO);
                    }
                    Value addr = build_factory.buildGEP(cur_block, tmp_value, index_list);
                    visitExp(stmt.getExp_single());
                    tmp_value = build_factory.buildStore(cur_block, addr, tmp_value);
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
            case IF:
                if (!stmt.Has_else()) { // 无else
                    /*
                    basic_block;
                    if (cond){true_block}
                    final_block;
                     */
                    BasicBlock basic_block = cur_block;

                    BasicBlock true_block = build_factory.buildBasicBlock(cur_func);
                    cur_block = true_block;
                    visitStmt(stmt.getStmtList().get(0));
                    BasicBlock final_block = build_factory.buildBasicBlock(cur_func);
                    build_factory.buildBranch(cur_block, final_block);

                    cur_true_block = true_block;
                    cur_false_block = final_block;
                    cur_block = basic_block;
                    visitCond(stmt.getCond());

                    cur_block = final_block;
                } else { // 有else
                    /*
                    basic_block;
                    if (cond){true_block...true_end_block}
                    else{false_block...false_end_block}
                    final_block;
                     */
                    BasicBlock basic_block = cur_block;

                    BasicBlock true_block = build_factory.buildBasicBlock(cur_func);
                    cur_block = true_block;
                    visitStmt(stmt.getStmtList().get(0));
                    BasicBlock true_end_block = cur_block;

                    BasicBlock false_block = build_factory.buildBasicBlock(cur_func);
                    cur_block = false_block;
                    visitStmt(stmt.getStmtList().get(1));
                    BasicBlock false_end_block = cur_block;

                    cur_true_block = true_block;
                    cur_false_block = false_block;
                    cur_block = basic_block;
                    visitCond(stmt.getCond());

                    BasicBlock final_block = build_factory.buildBasicBlock(cur_func);
                    build_factory.buildBranch(true_end_block, final_block);
                    build_factory.buildBranch(false_end_block, final_block);
                    cur_block = final_block;
                }
                break;
            case FOR:
                /*
                basic_block;
                for_stmt1_block;
                for(cond){
                    for_block;
                    ...
                    for_end_block;
                    for_stmt_2_block;
                }
                loop_final_block;

                 Translated as below:
                 is_cond:
                 basicBlock;
                 [for_stmt1_block];
                 if (judge——block1(cond)) {
                    goto for_block;
                 } else {
                    goto loop_final_block;
                 }
                 for_block;
                 [for_stmt_block2];
                 if (judge_block2(cond)) {
                    goto for_block;
                 }
                 loop_final_block;

                 no_cond:
                 basicBlock;
                 [for_stmt1_block];
                 for_block;
                 [for_stmt_block2];
                 goto for_block;
                 loop_final_block;
                 */
                if (stmt.getFor_stmt1()!=null){
                    visitForStmt(stmt.getFor_stmt1());
                }
                BasicBlock basic_block = cur_block;
                BasicBlock tmp_continue_block = continue_block;
                BasicBlock tmp_loop_final_block = cur_loop_final_block;
                if (stmt.getCond()!=null){
                    BasicBlock judge_block1 = build_factory.buildBasicBlock(cur_func);
                    BasicBlock for_block = build_factory.buildBasicBlock(cur_func);
                    BasicBlock judge_block2 = build_factory.buildBasicBlock(cur_func);
                    build_factory.buildBranch(cur_block, judge_block1);
                    cur_block = for_block;
                    tmp_continue_block = continue_block = judge_block2;
                    tmp_loop_final_block = cur_loop_final_block = build_factory.buildBasicBlock(cur_func);
                    if (stmt.getFor_stmt2()!=null){
                        tmp_continue_block = continue_block = build_factory.buildBasicBlock(cur_func);
                    }
                    visitStmt(stmt.getFor_stmt_body());
                    build_factory.buildBranch(cur_block, tmp_continue_block);
                    if (stmt.getFor_stmt2()!=null){
                        cur_block = tmp_continue_block;
                        visitForStmt(stmt.getFor_stmt2());
                    }
                    build_factory.buildBranch(cur_block, judge_block2);

                    cur_true_block = for_block;
                    cur_false_block = tmp_loop_final_block;
                    cur_block = judge_block1;
                    visitCond(stmt.getCond());

                    cur_true_block = for_block;
                    cur_false_block = tmp_loop_final_block;
                    cur_block = judge_block2;
                    visitCond(stmt.getCond());

                    cur_block = tmp_loop_final_block;
                } else { // 无cond
                    BasicBlock for_block = build_factory.buildBasicBlock(cur_func);
                    build_factory.buildBranch(cur_block, for_block); // TODO: 必要？
                    cur_block = for_block;
                    tmp_continue_block = continue_block = for_block;
                    tmp_loop_final_block = cur_loop_final_block = build_factory.buildBasicBlock(cur_func);
                    if (stmt.getFor_stmt2()!=null){
                        tmp_continue_block = continue_block = build_factory.buildBasicBlock(cur_func);
                    }
                    visitStmt(stmt.getFor_stmt_body());
                    build_factory.buildBranch(cur_block, tmp_continue_block);
                    if (stmt.getFor_stmt2()!=null){
                        cur_block = tmp_continue_block;
                        visitForStmt(stmt.getFor_stmt2());
                    }
                    build_factory.buildBranch(cur_block, for_block);
                    cur_block = tmp_loop_final_block;
                }
                break;
            case BREAK:
                build_factory.buildBranch(cur_block, cur_loop_final_block);
                break;
            case CONTINUE:
                build_factory.buildBranch(cur_block, continue_block);
                break;
            case RETURN:
                if (stmt.getExp_single()!=null){ // 有Exp
                    visitExp(stmt.getExp_single());
//                    Value value = tmp_value;
//                    value.setType(IntegerType.i32);
                    build_factory.buildReturn(cur_block, tmp_value);
                } else { // 无Exp
                    build_factory.buildReturn(cur_block);
//                    System.out.println(stmt.getExp_single());
                }
                break;
            case LVALASSIGNGETINT:
                if (stmt.getLVal().getExps().isEmpty()){
                    Value pointer = getValue(stmt.getLVal().getIdentString());
                    tmp_value = build_factory.buildCall(cur_block, (Function) getValue("getint"), new ArrayList<>());
                    build_factory.buildStore(cur_block, pointer, tmp_value);
                } else { // 数组
                    ArrayList<Value> index_list = new ArrayList<>();
                    for (Exp exp : stmt.getLVal().getExps()){
                        visitExp(exp);
                        index_list.add(tmp_value);
                    }
                    tmp_value = getValue(stmt.getLVal().getIdentString());
                    IRType type = tmp_value.getType();
                    IRType target_type = ((PointerType) type).getTargetType();
                    if (target_type instanceof PointerType){ // 形如 a[][1]
                        tmp_value = build_factory.buildLoad(cur_block, tmp_value);
                    } else { // 形如 a[1][2]
                        index_list.add(0, ConstInt.ZERO);
                    }
                    Value addr = build_factory.buildGEP(cur_block, tmp_value, index_list);
                    Value in = build_factory.buildCall(cur_block, (Function) getValue("getint"), new ArrayList<>());
                    tmp_value = build_factory.buildStore(cur_block, addr, in);
                }
                break;
            case PRINTF:
                Token format_string_token = stmt.getFormat_string_token();
                String format_string = format_string_token.token.replace("\\n","\n").replace("\"",""); // 将'\n'转置为换行符且去掉头尾不输出的两个引号
                ArrayList<Value> printf_args_values = new ArrayList<>();
                for (Exp exp : stmt.getPrintf_exp_list()){
                    visitExp(exp);
                    printf_args_values.add(tmp_value);
                }
                int pos = 0;
                for (pos=0; pos<format_string.length(); pos++){
                    if (format_string.charAt(pos)=='%'){ // 输出value
                        build_factory.buildCall(cur_block, (Function) getValue("putint"), new ArrayList<Value>(){{add(printf_args_values.remove(0));}}); // 取第一个exp，然后删除第一个exp
                        pos++;
                    } else { // 输出字符 TODO: CHAR TO STRING
                        if (char_to_string){

                        } else {
                            int i = pos; // 因为内部类需要final类型或者实际上是final的
                            build_factory.buildCall(cur_block, (Function) getValue("putch"), new ArrayList<Value>(){{add(build_factory.getConstInt(format_string.charAt(i)));}});
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Cond → LOrExp
     */
    public void visitCond(Cond cond) {
        visitLOrExp(cond.getLOrExp());
    }

    /**
     * LOrExp → LAndExp | LOrExp '||' LAndExp
     */
    public void visitLOrExp(LOrExp lOrExp) {
        ArrayList<Token> landexp_list = lOrExp.getLAndExp_list();
        for (int i=0; i<landexp_list.size(); i+=2){
            BasicBlock true_block = cur_true_block;
            BasicBlock false_block = cur_false_block;
            BasicBlock tmp_false_block = cur_false_block;
            BasicBlock then_block = null;
            if (i<landexp_list.size()-2){
                then_block = build_factory.buildBasicBlock(cur_func);
                tmp_false_block = then_block;
            }
            cur_false_block = tmp_false_block;
            visitLAndExp((LAndExp)landexp_list.get(i).nt);
            cur_true_block = true_block;
            cur_false_block = false_block;
            if (i<landexp_list.size()-2){
                cur_block = then_block;
            }
        }
    }

    /**
     * LAndExp → EqExp | LAndExp '&&' EqExp
     */
    public void visitLAndExp(LAndExp lAndExp) {
        ArrayList<Token> eqexp_list = lAndExp.getEqExp_list();
        for (int i=0; i<eqexp_list.size(); i+=2){
            BasicBlock true_block = cur_true_block;
            BasicBlock false_block = cur_false_block;
            BasicBlock tmp_true_block = cur_true_block;
            BasicBlock then_block = null;
            if (i<eqexp_list.size()-2){
                then_block = build_factory.buildBasicBlock(cur_func);
                tmp_true_block = then_block;
            }
            cur_true_block = tmp_true_block;
            tmp_value = null;
            visitEqExp((EqExp)eqexp_list.get(i).nt);
            build_factory.buildBranch(cur_block, tmp_value, cur_true_block, cur_false_block);
            cur_true_block = true_block;
            cur_false_block = false_block;
            if (i<eqexp_list.size()-2){
                cur_block = then_block;
            }
        }
    }

    /**
     * EqExp → RelExp | EqExp ('==' | '!=') RelExp
     */
    public void visitEqExp(EqExp eqExp){
        ArrayList<Token> relexp_list = eqExp.getRelExp_list();
        for (int i=0; i<relexp_list.size(); i+=2){
            Value value = tmp_value;
            IROp op = tmp_op;
            tmp_value = null;
            visitRelExp((RelExp)relexp_list.get(i).nt);
            if (value != null){
                tmp_value = build_factory.buildBinary(cur_block, op, value, tmp_value);
            }
            if (i<relexp_list.size()-2){
                tmp_op = relexp_list.get(i+1).type.equals(LexType.EQL) ? IROp.Eq : IROp.Ne;
            }
        }
    }

    /**
     * RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     */
    public void visitRelExp(RelExp relExp){
        ArrayList<Token> addexp_list = relExp.getAddExp_list();
        for (int i=0; i<addexp_list.size(); i+=2){
            Value value = tmp_value;
            IROp op = tmp_op;
            tmp_value = null;
            visitAddExp((AddExp) addexp_list.get(i).nt);
            if (value != null){
                tmp_value = build_factory.buildBinary(cur_block, op, value, tmp_value);
            }
            if (i<addexp_list.size()-2){
                switch (addexp_list.get(i+1).type){
                    case LSS -> tmp_op = IROp.Lt;
                    case LEQ -> tmp_op = IROp.Le;
                    case GRE -> tmp_op = IROp.Gt;
                    case GEQ -> tmp_op = IROp.Ge;
                }
            }
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
                value = tmp_value = build_factory.buildBinary(cur_block, op, value, tmp_value);
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
                value = tmp_value = build_factory.buildBinary(cur_block, op, value, tmp_value);
            }
        }
    }

    /**
     * UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
     */
    public void visitUnaryExp(UnaryExp unary_exp) {
        if (unary_exp.getPrimaryExp()!=null){ // PrimaryExp
            visitPrimaryExp(unary_exp.getPrimaryExp());
        } else if (unary_exp.getUnaryOp()!=null){ // UnaryOp UnaryExp
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
                case NOT -> {
                    visitUnaryExp(unary_exp.getUnaryExp());
                    tmp_value = build_factory.buildNot(cur_block, tmp_value);
                }
            }
        } else { // Ident '(' [FuncRParams] ')'
            func_rparam_list = new ArrayList<>();
            if (unary_exp.getFuncRParams()!=null){ // 有实参的函数
                visitFuncRParams(unary_exp.getFuncRParams());
            }
            tmp_value = build_factory.buildCall(cur_block, (Function) getValue(unary_exp.getIdentString()), func_rparam_list);
        }
    }

    /**
     * FuncRParams → Exp { ',' Exp }
     */
    public void visitFuncRParams(FuncRParams funcRParams) {
        ArrayList<Value> values = new ArrayList<>();
        for (Exp exp : funcRParams.getExpList()){
            visitExp(exp);
            values.add(tmp_value);
        }
        func_rparam_list = values;
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
     * LVal → Ident {'[' Exp ']'} // 1.普通变量 2.一维数组 3.二维数组
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
                if (l==null){ // 符号表中没有指定的变量
                    System.out.println("error in LLVMGen, LVal");
                }
                IRType type = l.getType(); // 前面已经过错误处理，保证非空
                if (!(((PointerType) type).getTargetType() instanceof ArrayType)){
                    tmp_value = build_factory.buildLoad(cur_block, tmp_value);
                } else { //
                    ArrayList<Value> indexList = new ArrayList<>();
                    indexList.add(ConstInt.ZERO);
                    indexList.add(ConstInt.ZERO);
                    tmp_value = build_factory.buildGEP(cur_block, tmp_value, indexList);
                }
            } else { // 数组
                ArrayList<Value> index_list = new ArrayList<>();
                for (Exp exp : lVal.getExps()){
                    visitExp(exp);
                    index_list.add(tmp_value);
                }
                tmp_value = getValue(lVal.getIdentString());
                IRType type = tmp_value.getType(); // 前面已经过错误处理，保证非空
                IRType target_type = ((PointerType) type).getTargetType();
                if (target_type instanceof PointerType){ // 形如 a[][1]
                    tmp_value = build_factory.buildLoad(cur_block, tmp_value);
                } else { // 形如 a[0][1]
                    index_list.add(0, ConstInt.ZERO);
                }
                Value addr = build_factory.buildGEP(cur_block, tmp_value, index_list);
                if (((PointerType)addr.getType()).getTargetType() instanceof ArrayType){
                    ArrayList<Value> index_list_2 = new ArrayList<>();
                    index_list_2.add(ConstInt.ZERO);
                    index_list_2.add(ConstInt.ZERO);
                    tmp_value = build_factory.buildGEP(cur_block, addr, index_list_2);
                } else {
                    tmp_value = build_factory.buildLoad(cur_block, addr);
                }
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
