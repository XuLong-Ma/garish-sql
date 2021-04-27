package org.dregs.garish.sql.utils;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ASTUtil {

    private Messager messager;

    private JavacTrees javacTrees;

    private Context context;

    private TreeMaker treeMaker;

    private Names names;

    public ASTUtil(Messager messager, JavacTrees javacTrees, Context context, TreeMaker treeMaker, Names names) {
        this.messager = messager;
        this.javacTrees = javacTrees;
        this.context = context;
        this.treeMaker = treeMaker;
        this.names = names;
    }

    public JCTree.JCExpression Class(Class c){
        return Class(c.getName());
    }
    public JCTree.JCExpression Class(String components){
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
    }

    public JCTree.JCImport Import(Class c, boolean isStatic){
        return Import(c.getName(),isStatic);
    }

    public JCTree.JCImport Import(String classPath, boolean isStatic){
        String[] sorts = classPath.split("\\.");
        StringBuilder builder = new StringBuilder(sorts[0]);
        for(int $i=1;$i<sorts.length-1;$i++){
            builder.append(".").append(sorts[$i]);
        }
        String packageName = builder.toString();
        String className = sorts[sorts.length-1];
        return treeMaker.Import(
                treeMaker.Select(
                        treeMaker.Ident(names.fromString(packageName)),
                        names.fromString(className)),
                isStatic);
    }

    public <T,A extends Annotation> Set<T> glossArrayToSet(Function<A,T[]> function,A a){
        if(null == a)return new HashSet<T>();
        T[] apply = function.apply(a);
        return new HashSet<T>(Arrays.asList(apply));
    }

    public JCTree.JCMethodDecl MethodDef(long flags, String name, String className, List<JCTree.JCTypeParameter> var4, List<JCTree.JCVariableDecl> var5, List<JCTree.JCExpression> var6, JCTree.JCBlock block, JCTree.JCExpression var8){
        return treeMaker.MethodDef(treeMaker.Modifiers(flags), names.fromString(name), Class(className), var4,var5, var6, block, var8);
    }
    public JCTree.JCMethodDecl MethodDef(long flags, String name, JCTree.JCExpression jcExpression, List<JCTree.JCTypeParameter> var4, List<JCTree.JCVariableDecl> var5, List<JCTree.JCExpression> var6, JCTree.JCBlock block, JCTree.JCExpression var8){
        return treeMaker.MethodDef(treeMaker.Modifiers(flags), names.fromString(name), jcExpression, var4,var5, var6, block, var8);
    }
    public JCTree.JCMethodDecl MethodDef(long flags, String name, Class c, List<JCTree.JCTypeParameter> var4, List<JCTree.JCVariableDecl> var5, List<JCTree.JCExpression> var6, JCTree.JCBlock var7, JCTree.JCExpression var8){
        return MethodDef(flags, name, c.getName(), var4, var5, var6, var7, var8);
    }
    public JCTree.JCArrayTypeTree ArrayType(String className){
        return treeMaker.TypeArray(Class(className));
    }
    public JCTree.JCArrayTypeTree ArrayType(Class c){
        return treeMaker.TypeArray(Class(c));
    }


}
