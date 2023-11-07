import DataStructure.AST;
import Lexer.*;
import Exception.*;
import Parser.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * 编译器
 *
 * @author Stevex
 * @date 2023/09/22
 */
public class Compiler {
    /**
     * 源代码文件路径
     */
    static String source_code = "testfile.txt";
    /**
     * 文件输出路径
     */
    static String file_output = "output.txt";
    /**
     * 源代码字符串
     */
    static String source_code_string = "";
    /**
     * token列表
     */
    static ArrayList<Token> token_list = new ArrayList<>();

    static AST ast = new AST();

    public static ArrayList<Token> ast_post_root_traverse = new ArrayList<>();

    /**
     * @param source_code 源代码文件路径
     * @throws IOException
     */
    static void getSourceCodeString(String source_code) throws IOException {
        Path source_code_path = Paths.get(source_code);
        source_code_string = Files.readString(source_code_path);
    }

    /**
     * 输出语法分析后的token列表
     *
     * @throws IOException IO异常
     */
    static void writeTokenList() throws IOException {
        FileWriter writer;
        writer = new FileWriter(file_output);
        for (Token token:ast_post_root_traverse){
            String str;
            if (token.type==null) {
                str = token.nt.getNtType().ntEnumGetWord();
            } else {
                str = token.type.lexEnumGetType() + ' ' + token.token;
            }
            writer.write(str+'\n');
        }
        writer.flush();
        writer.close();
    }

    /**
     * 主要
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        try{
            getSourceCodeString(source_code);
            Lexer lexer = new Lexer(source_code_string,token_list);
            Parser parser = new Parser(lexer,ast,token_list,ast_post_root_traverse);
            writeTokenList();
        } catch (CompilerException e) {
            if (!e.getType().equals("0")){
                System.out.println("Exception.CompilerException:"+e.getType()+' '+e.getLine()+' '+e.getMessage());
            } else {
                System.out.println("Exception.CompilerException:"+e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("IOException:"+e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception:"+e.getMessage());
        }
    }
}
