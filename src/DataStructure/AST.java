package DataStructure;

import Lexer.Token;
import Parser.NonTerminalType;

import java.util.ArrayList;

public class AST {
    private ASTNode root;  //树的根节点
    public AST(){      //构造一棵空树
    }
    public AST(ASTNode root){   //构造一棵树
        this.root=root;
    }
    public void preRootTraverse(ASTNode t){  //树的先根遍历
        if(t!=null){
            System.out.print(t.getData());
            preRootTraverse(t.getFirstChild());
            preRootTraverse(t.getNextSibling());
        }
    }
    public void postRootTraverse(ASTNode t){  //树的后根遍历
        if(t!=null){
            postRootTraverse(t.getFirstChild());
            System.out.print(t.getData());
            postRootTraverse(t.getNextSibling());
        }
    }
    public void postRootTraverseToList(ASTNode t, ArrayList<Token> ast_post_root_traverse){  //树的后根遍历
        if(t!=null){
            postRootTraverseToList(t.getFirstChild(), ast_post_root_traverse);
//            System.out.print(t.getData());
            Token token = (Token) t.getData();
            if (!(token.equalNonTerminalType(NonTerminalType.BLOCKITEM)||token.equalNonTerminalType(NonTerminalType.DECL)||token.equalNonTerminalType(NonTerminalType.BTYPE))) // 不输出的语法树节点:<BlockItem>, <Decl>, <BType>
                ast_post_root_traverse.add(token);
            postRootTraverseToList(t.getNextSibling(), ast_post_root_traverse);
        }
    }

    public void setRoot(ASTNode root) {
        this.root = root;
    }
    public ASTNode getRoot() {
        return this.root;
    }
//    public void leveltraverse(ASTNode t){   //树的层次遍历
//        if(t!=null){
//            Linkqueue l=new Linkqueue();
//            l.offer(t);
//            while(!l.isEmpty()){
//                for(t=(ASTNode)t.poll();t!=null;t=t.getnextsibling())
//                    System.out.print(t.getdata()+" ");
//                if(t.getfirstchild()!=null)
//                    l.offer(t.getfirstchild());
//            }
//        }
//    }
    public AST createcstree() throws Exception{   //创建树
        ASTNode k=new ASTNode('k',null,null);
        ASTNode f=new ASTNode('f',k,null);
        ASTNode e=new ASTNode('e',null,f);
        ASTNode g=new ASTNode('g',null,null);
        ASTNode l=new ASTNode('l',null,null);
        ASTNode j=new ASTNode('j',null,null);
        ASTNode i=new ASTNode('i',l,j);
        ASTNode h=new ASTNode('h',null,i);
        ASTNode d=new ASTNode('d',h,null);
        ASTNode c=new ASTNode('c',g,d);
        ASTNode b=new ASTNode('b',e,c);
        ASTNode a=new ASTNode('a',b,null);
        return new AST(a);   //创建根节点为a的树
    }
//    public static void main(String[] args){
//        AST debug=new AST();
//        AST cs=debug.createcstree();
//        ASTNode root=cs.root;  //取得树的根节点
//        System.out.println("树的先根遍历");
//        cs.preRootTraverse(root);
//        System.out.println();
//        System.out.println("树的后根遍历");
//        cs.postRootTraverse(root);
//    }
}
