package DataStructure;

import java.util.ArrayList;

/**
 * 错误报告器
 *
 * @author Stevex
 * @date 2023/11/08
 */
public class ErrorReporter {
    static boolean report_error;
    static ArrayList<ErrorReport> error_list = new ArrayList<>();

    public static void setErrorReporter(boolean report_error, ArrayList<ErrorReport>error_list) {
        ErrorReporter.report_error = report_error;
        ErrorReporter.error_list = error_list;
    }
    public static void reportError(int line, ErrorType errorType){
        if (report_error){
            int error_list_size = error_list.size();
            if (error_list_size==0 || error_list.get(error_list_size-1).line!=line){ // 每行仅有一个错误
                error_list.add(new ErrorReport(line, errorType));
            }
        }
    }
}
