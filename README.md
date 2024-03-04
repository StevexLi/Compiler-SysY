# Compiler-SysY 
A SysY(similar to C) Language Compiler. Object code can be LLVM or MIPS. BUAA Compiling Class work. 

本人的编译器由Java语言编写，支持将SysY语言（一种类C语言）编写的源代码翻译为LLVM中间代码和MIPS目标代码，并支持在中端和后端对生成的代码进行一定的优化。  

本人的编译器主要分为6个package以及1个入口程序，他们共同组成一个拥有前端、中端和后端的完整的编译器。

各部分的简要介绍如下：  

（1） Compiler.java：编译器的入口程序。负责进行IO操作，编译选项设置和调用编译器其他各部分进行分析。  
（2） Lexer：词法分析部分。输入源程序的字符串，返回Token列表。  
（3） Parser：语法语法分析部分。输入Token列表，返回抽象语法树。同时在建立抽象语法树的过程中进行错误处理，若源代码存在错误，则生成错误列表。  
（4） Ir：语义分析及中端代码生成部分。输入并遍历抽象语法树，返回LLVM字符串。此外，本包中还有中端优化器，可以根据编译选项设置决定是否进行优化。  
（5） BackEnd：目标代码生成部分。输入中端代码生成器的IRModule，输出对应的MIPS程序。生成MIPS程序的过程中，可以指定是否进行乘除优化。  
（6） DataStructure：定义编译器所需的数据结构。如Token，抽象语法树节点等。  
（7） Exception：自定义的异常类型，用于向用户提供编译器的异常信息。  
