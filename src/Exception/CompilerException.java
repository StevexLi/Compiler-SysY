package Exception;

public class CompilerException extends Exception{
    String type = "0";
    int line;

    public CompilerException(String str){
        super(str);
    }
    public CompilerException(String type, int line, String str){
        super(str);
        this.type = type;
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public int getLine() {
        return line;
    }
}
