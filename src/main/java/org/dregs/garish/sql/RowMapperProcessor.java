package org.dregs.garish.sql;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import org.dregs.garish.sql.annotation.AutoRowMapper;
import org.dregs.garish.sql.annotation.Cell;
import org.dregs.garish.sql.annotation.EasyInsert;
import org.dregs.garish.sql.annotation.Entity;
import org.dregs.garish.sql.annotation.GeneratedValue;
import org.dregs.garish.sql.annotation.Ignore;
import org.dregs.garish.sql.data.Tuple;
import org.dregs.garish.sql.data.Tuple2;
import org.dregs.garish.sql.utils.ASTUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Set;


@SupportedAnnotationTypes("org.dregs.garish.sql.annotation.AutoRowMapper")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RowMapperProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacTrees javacTrees;
    private Context context;
    private TreeMaker treeMaker;
    private Names names;
    private ASTUtil astUtil;

    public RowMapperProcessor() {
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.javacTrees = JavacTrees.instance(processingEnv);
        this.context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(this.context);
        this.names = Names.instance(context);
        this.astUtil = new ASTUtil(messager, javacTrees, context, treeMaker, names);

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(AutoRowMapper.class);
        set.forEach(element -> {
            AutoRowMapper autoRowMapper = element.getAnnotation(AutoRowMapper.class);
            Entity entity = element.getAnnotation(Entity.class);
            if(null == entity){
                messager.printMessage(Diagnostic.Kind.ERROR,"请检查映射类是否设置Entity",element);
                return;
            }
            String tableName = entity.name();
            boolean isChange = autoRowMapper.value();
            boolean isInsert = null != element.getAnnotation(EasyInsert.class);
            Ignore ignore = element.getAnnotation(Ignore.class);
            Set<String> ignores = astUtil.glossArrayToSet(Ignore::names,ignore);
            JCTree.JCCompilationUnit imports = (JCTree.JCCompilationUnit) javacTrees.getPath(element).getCompilationUnit();
            imports.defs = add(imports.defs,astUtil.Import(Cell.class,false));
            JCTree jcTree = javacTrees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    ArrayList<Tuple2<String,JCTree.JCExpression>> cellNameAndVarList = new ArrayList<>();
                    ArrayList<String> cellList = new ArrayList<>();
                    for (JCTree tree : jcClassDecl.defs) {
                        if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                            code:{
                            JCTree.JCVariableDecl variable = (JCTree.JCVariableDecl) tree;
                            String name = variable.name.toString();
                            if(ignores.contains(name))continue;
                            JCTree.JCFieldAccess aThis = treeMaker.Select(treeMaker.Ident(names.fromString("this")), variable.getName());
                            List<JCTree.JCAnnotation> annotationsList = variable.mods.getAnnotations();
                            String value = name;
                            boolean isExist = false;
                            boolean isInsert = true;
                            for(JCTree.JCAnnotation jcAnnotation : annotationsList){
                                JCTree annotationType = jcAnnotation.annotationType;
                                if(null == annotationType)continue ;
                                Type type = annotationType.type;
                                if(null == type)continue ;
                                String annotationName = type.toString();
                                if(annotationName.lastIndexOf(Ignore.class.getName()) != -1)break code;
                                if(annotationName.lastIndexOf(GeneratedValue.class.getName()) != -1)isInsert = false;
                                if(annotationName.lastIndexOf(Cell.class.getName()) != -1){
                                    isExist = true;
                                    value = jcAnnotation.attribute.member(names.fromString("name")).getValue().toString();
                                }
                            }
                            if(!isExist){
                                if(isChange){
                                    StringBuilder result = new StringBuilder();
                                    for(int $i = 0 ; $i < name.length() ; $i++){ if(Character.isUpperCase(name.charAt($i))){ result.append("_").append(Character.toLowerCase(name.charAt($i))); }else{ result.append(name.charAt($i)); } }
                                    value = result.toString();
                                }
                                variable.mods.annotations = add(variable.mods.annotations, treeMaker.Annotation(
                                        treeMaker.Ident(names.fromString("Cell")),
                                        List.of(treeMaker.Assign(treeMaker.Ident(names.fromString("name")),
                                                treeMaker.Literal(value))))
                                );
                            }
                            cellList.add(String.format("`%s`",value));
                            if(isInsert) cellNameAndVarList.add(Tuple.initialize(value,aThis));
                        }
                        }
                    }
                    List<JCTree.JCExpression> valueList = List.<JCTree.JCExpression>nil();
                    ArrayList keyList = new ArrayList<String>();
                    ArrayList placeholderList = new ArrayList<String>();
                    for(Tuple2<String,JCTree.JCExpression> tuple : cellNameAndVarList){
                        keyList.add(String.format("`%s`",tuple._1()));
                        placeholderList.add("?");
                        valueList = valueList.append(tuple._2());
                    }
                    String insertSQL = String.format(
                            "INSERT INTO `%s` (%s) VALUES (%s)",
                            tableName,
                            String.join(",", keyList),
                            String.join(",", placeholderList)
                    );
                    String cells = String.join(",", cellList);

                    ListBuffer<JCTree.JCStatement> $cellsStatements = new ListBuffer<>();
                    $cellsStatements.append(
                            treeMaker.Return(
                                    treeMaker.Literal(cells)
                            )
                    );
                    JCTree.JCBlock $cellsBody = treeMaker.Block(0, $cellsStatements.toList());
                    JCTree.JCMethodDecl $cells = astUtil.MethodDef(Flags.PUBLIC, "$cells", String.class, List.nil(), List.nil(), List.nil(), $cellsBody, null);
                    jcClassDecl.defs = jcClassDecl.defs.prepend($cells);

                    ListBuffer<JCTree.JCStatement> $insertSQLStatements = new ListBuffer<>();
                    $insertSQLStatements.append(
                            treeMaker.Return(
                                    treeMaker.Literal(insertSQL)
                            )
                    );
                    JCTree.JCBlock body = treeMaker.Block(0, $insertSQLStatements.toList());
                    JCTree.JCMethodDecl $insertSQL = astUtil.MethodDef(Flags.PUBLIC, "$insertSQL", String.class, List.nil(), List.nil(), List.nil(), body, null);
                    jcClassDecl.defs = jcClassDecl.defs.prepend($insertSQL);

                    JCTree.JCNewArray jcNewArray = treeMaker.NewArray(astUtil.Class("java.lang.Object"),List.<JCTree.JCExpression>nil().prepend(treeMaker.Literal(valueList.size())) , valueList);
                    ListBuffer<JCTree.JCStatement> $objectsStatements = new ListBuffer<>();
                    $objectsStatements.append(
                            treeMaker.Return(
                                    jcNewArray
                            )
                    );
                    JCTree.JCBlock block = treeMaker.Block(0, $objectsStatements.toList());
                    JCTree.JCMethodDecl $objects = astUtil.MethodDef(Flags.PUBLIC, "$objects", astUtil.ArrayType(Object.class), List.nil(), List.nil(), List.nil(), block, null);
                    jcClassDecl.defs = jcClassDecl.defs.prepend($objects);
                    super.visitClassDef(jcClassDecl);
                }
            });
        });


        return true;
    }

    private <T> List<T> add(List<T> list,T  t){
        return list = list.append(t);
    }



}
