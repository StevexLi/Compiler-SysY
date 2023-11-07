package DataStructure;

public class ASTNode { //孩子兄弟结点cstree
    private Object data; //结点的数据域
    private ASTNode firstchild,nextsibling;  //左孩子，右兄弟
    public ASTNode() throws Exception {   //构造一个空结点
        this(null);
    }
    public ASTNode(Object data) throws Exception{   //构造一个左孩子，右兄弟为空的结点
        this(data,null,null);
        if (data!=null && data.getClass().equals(Token.class)) {
            this.firstchild = ((Token) data).firstchild;
            this.nextsibling = ((Token) data).nextsibling;
        }
    }
    public ASTNode(Object data, ASTNode firstchild, ASTNode nextsibling) throws Exception{
        this.data=data;
        this.firstchild=firstchild;
        this.nextsibling=nextsibling;
    }
    public Object getData(){
        return data;
    }
    public ASTNode getFirstChild(){
        return firstchild;
    }
    public ASTNode getNextSibling(){
        return nextsibling;
    }
    public void setData(Object data){
        this.data=data;
    }
    public void setFirstChild(ASTNode firstchild){
        this.firstchild=firstchild;
    }
    public void setNextSibling(ASTNode nextsibling){
        if (data!=null)
            this.nextsibling=nextsibling;
    }
}
