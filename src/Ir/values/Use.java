package Ir.values;

/**
 * 维护def-use关系
 *
 * @author Stevex
 * @date 2023/11/21
 */
public class Use {
    private User user; // 使用此Use的Value
    private Value value; // 此Use使用的Value
    private int op_pos; // 在OpList中的位置

    public Use(Value value, User user, int op_pos){
        this.user = user;
        this.value = value;
        this.op_pos = op_pos;
    }

    public int getPosOfOperand() {
        return op_pos;
    }

    public User getUser() {
        return user;
    }
}
