package Parser;

public class NonTerminal {
    public NonTerminalType nt_type;
    public ASTNode firstchild;
    public ASTNode nextsibling;

    public NonTerminalType getNtType() {
        return nt_type;
    }

    public void setFirstchild(ASTNode firstchild) {
        if (this.firstchild==null)
            this.firstchild = firstchild;
    }

    public void setNextsibling(ASTNode nextsibling) {
        this.nextsibling = nextsibling;
    }

    @Override
    public String toString() {
        return nt_type.ntEnumGetWord()+' '+nt_type.ntEnumGetType();
    }
}
