package lib.annotation.processor;

/**
 * @auther yuansui
 * @since 2017/8/1
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import lib.annotation.AutoIntent;
import lib.annotation.Extra;

@AutoService(Processor.class)
public class AutoIntentProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(AutoIntent.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(AutoIntent.class)) {
            // Make sure element is a field or a method declaration
            if (!annotatedElement.getKind().isClass()) {
                error(annotatedElement, "Only classes can be annotated with @%s", AutoIntent.class.getSimpleName());
                return true;
            }

            try {
                TypeSpec builderSpec = getBuilderSpec(annotatedElement);
                JavaFile builderFile = JavaFile.builder(getPackageName(annotatedElement), builderSpec).build();
                builderFile.writeTo(getFiler());
            } catch (Exception e) {
                error(annotatedElement, "Could not create intent builder for %s: %s", annotatedElement.getSimpleName(), e.getMessage());
            }
        }
        return true;
    }

    private void error(Element e, String msg, Object... args) {
        getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private String getPackageName(Element e) {
        while (!(e instanceof PackageElement)) {
            e = e.getEnclosingElement();
        }
        return ((PackageElement) e).getQualifiedName().toString();
    }

    private TypeSpec getBuilderSpec(Element annotatedElement) {
        List<Element> required = new ArrayList<>();
        List<Element> optional = new ArrayList<>();
        List<Element> all = new ArrayList<>();

        getAnnotatedFields(annotatedElement, required, optional);
        all.addAll(required);
        all.addAll(optional);

        TypeName annotatedTypeName = TypeName.get(annotatedElement.asType());

        final String name = String.format("%sIntent", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        /**
         * 生成构造方法
         */
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        builder.addMethod(constructor.build());

        ClassName clzName = ClassName.get(getPackageName(annotatedElement), name);

        /**
         * 生成create方法
         */
        String simpleName = clzName.simpleName();
        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement(simpleName + " builder = new " + simpleName + "()")
                .returns(clzName);
        for (Element e : required) {
            String paramName = getParamName(e);
            builder.addField(TypeName.get(e.asType()), paramName, Modifier.PRIVATE);
            createMethod.addParameter(TypeName.get(e.asType()), paramName);
            createMethod.addStatement("builder.$N = $N", paramName, paramName);
        }
        createMethod.addStatement("return builder");
        builder.addMethod(createMethod.build());

        /**
         * 根据optional生成链式调用的方法
         */
        for (Element e : optional) {
            String paramName = getParamName(e);
            builder.addField(TypeName.get(e.asType()), paramName, Modifier.PRIVATE);
            builder.addMethod(MethodSpec.methodBuilder(paramName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(e.asType()), paramName)
                    .addStatement("this.$N = $N", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.get(getPackageName(annotatedElement), name))
                    .build());
        }

        /**
         * 生成new intent方法
         */
        MethodSpec.Builder newIntentMethod = MethodSpec.methodBuilder("newIntent")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Context.class, "context")
                .addStatement("$T intent = new Intent(context, $T.class)", Intent.class, annotatedTypeName)
                .returns(Intent.class);
        for (Element e : required) {
            String paramName = getParamName(e);
            newIntentMethod.addParameter(TypeName.get(e.asType()), paramName);
        }
        addIntentStatement(newIntentMethod, all);
        newIntentMethod.addStatement("return intent");
        builder.addMethod(newIntentMethod.build());

        if (annotatedTypeName.toString().toLowerCase().contains("serv")) {
            // 服务
            /**
             * 生成 start 方法
             */
            MethodSpec.Builder startMethod = MethodSpec.methodBuilder("start")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Context.class, "context")
                    .addStatement("$T intent = new Intent(context, $T.class)", Intent.class, annotatedTypeName);
            addIntentStatement(startMethod, all);
            startMethod.addStatement("context.startService(intent)");
            builder.addMethod(startMethod.build());
        } else {
            // activity
            /**
             * 生成 start 方法
             */
            MethodSpec.Builder startMethod = MethodSpec.methodBuilder("start")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Context.class, "context")
                    .addStatement("$T intent = new Intent(context, $T.class)", Intent.class, annotatedTypeName);
            addIntentStatement(startMethod, all);
            startMethod.beginControlFlow("if (!(context instanceof $T))", Activity.class)
                    .addStatement("intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)")
                    .endControlFlow()
                    .addStatement("context.startActivity(intent)");
            builder.addMethod(startMethod.build());

            /**
             * 生成 start for result 方法
             */
            MethodSpec.Builder startForResultMethod = MethodSpec.methodBuilder("startForResult")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Object.class, "host")
                    .addParameter(TypeName.INT, "code")
                    .beginControlFlow("if (!(host instanceof $T) || !(host instanceof $T))", Activity.class, Fragment.class)
                    .addStatement("throw new $T()", IllegalStateException.class)
                    .endControlFlow()
                    .addStatement("$T intent = new Intent((Context) host, $T.class)", Intent.class, annotatedTypeName);
            addIntentStatement(startForResultMethod, all);
            startForResultMethod.beginControlFlow("if (host instanceof $T)", Activity.class)
                    .addStatement("(($T) host).startActivityForResult(intent, code)", Activity.class)
                    .nextControlFlow("else if (host instanceof $T)", Fragment.class)
                    .addStatement("(($T) host).startActivityForResult(intent, code)", Fragment.class)
                    .endControlFlow();
            builder.addMethod(startForResultMethod.build());
        }

        /**
         * 生成host调用的注入方法
         */
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(annotatedTypeName, "host")
                .addParameter(Intent.class, "intent")
                .addStatement("$T extras = intent.getExtras()", Bundle.class);
        for (Element e : all) {
            String paramName = getParamName(e);
            injectMethod.beginControlFlow("if (extras.containsKey($S))", paramName)
                    .addStatement("host.$N = ($T) extras.get($S)", e.getSimpleName().toString(), e.asType(), paramName)
                    .nextControlFlow("else");

            Extra extra = e.getAnnotation(Extra.class);
            TypeName typeName = TypeName.get(e.asType());
            if (typeName == TypeName.LONG) {
                injectMethod.addStatement("host.$N = " + extra.defaultLong(), e.getSimpleName().toString());
            } else if (typeName == TypeName.INT) {
                injectMethod.addStatement("host.$N = " + extra.defaultInt(), e.getSimpleName().toString());
            } else if (typeName == TypeName.FLOAT) {
                injectMethod.addStatement("host.$N = " + extra.defaultFloat(), e.getSimpleName().toString());
            } else if (typeName == TypeName.BOOLEAN) {
                injectMethod.addStatement("host.$N = " + extra.defaultBoolean(), e.getSimpleName().toString());
            } else {
                // Object
                injectMethod.addStatement("host.$N = null", e.getSimpleName().toString());
            }
            injectMethod.endControlFlow();
        }
        builder.addMethod(injectMethod.build());

        /**
         * 生成get各个参数的方法. 暂时不用
         */
//        for (Element e : all) {
////            String paramName = e.getSimpleName().toString(); // 这是原来的代码
//            String paramName = getParamName(e);
//            MethodSpec.Builder getterMethod = MethodSpec
//                    .methodBuilder("get" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1))
//                    .returns(ClassName.get(e.asType()))
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .addParameter(Intent.class, "intent")
//                    .addStatement("$T extras = intent.getExtras()", Bundle.class)
//                    .beginControlFlow("if (extras.containsKey($S))", paramName)
//                    .addStatement("return ($T) extras.get($S)", e.asType(), paramName)
//                    .nextControlFlow("else")
//                    .addStatement("return null")
//                    .endControlFlow();
//            builder.addMethod(getterMethod.build());
//        }

        return builder.build();
    }

    @TargetApi(VERSION_CODES.GINGERBREAD)
    private String getParamName(Element e) {
        String extraValue = e.getAnnotation(Extra.class).value();
        String ret = extraValue != null && !extraValue.trim().isEmpty() ? extraValue : e.getSimpleName().toString();
        if (ret.length() >= 2 && ret.startsWith("m")) {
            if (Pattern.compile("[A-Z]").matcher(ret.substring(1, 2)).matches()) {
                // 去掉m开头和首字母的大写
                String sub = ret.substring(1, 2);
                ret = ret.substring(1);
                ret = ret.replaceFirst(sub, sub.toLowerCase());
            }
        }

        return ret;
    }

    private void getAnnotatedFields(Element annotatedElement, List<Element> required, List<Element> optional) {
        for (Element e : annotatedElement.getEnclosedElements()) {
            Extra a = e.getAnnotation(Extra.class);
            if (a != null) {
                if (a.optional()) {
                    optional.add(e);
                } else {
                    required.add(e);
                }
            }
        }

        List<? extends TypeMirror> superTypes = getTypeUtils().directSupertypes(annotatedElement.asType());
        TypeMirror superClassType = superTypes.size() > 0 ? superTypes.get(0) : null;
        Element superClass = superClassType == null ? null : getTypeUtils().asElement(superClassType);
        if (superClass != null && superClass.getKind() == ElementKind.CLASS) {
            getAnnotatedFields(superClass, required, optional);
        }
    }


    private void addIntentStatement(MethodSpec.Builder builder, List<Element> all) {
        for (Element e : all) {
            String paramName = getParamName(e);
            builder.addStatement("intent.putExtra($S, $N)", paramName, paramName);
        }
    }
}