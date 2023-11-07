package DataStructure;

/**
 * 错误报告类
 *
 * @author Stevex
 * @date 2023/11/08
 */
public class ErrorReport {
    public int line;
    public ErrorType type;

    ErrorReport(int line, ErrorType errorType) {
        this.line = line;
        this.type = errorType;
    }
}
