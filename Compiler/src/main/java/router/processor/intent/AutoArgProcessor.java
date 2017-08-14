package router.processor.intent;

/**
 * @auther yuansui
 * @since 2017/8/1
 */

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

import router.android.AndroidClassName;
import router.android.JavaClassName;
import router.annotation.Arg;
import router.annotation.AutoArg;
import router.processor.BaseProcessor;

@AutoService(Processor.class)
public class AutoArgProcessor extends BaseProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return AutoArg.class;
    }

    @Override
    protected TypeSpec getBuilderSpec(Element annotatedElement) {
        List<Element> required = new ArrayList<>();
        List<Element> optional = new ArrayList<>();
        List<Element> all = new ArrayList<>();

        getAnnotatedFields(annotatedElement, required, optional);
        all.addAll(required);
        all.addAll(optional);

        TypeName annotatedTypeName = getTypeName(annotatedElement);

        final String name = String.format("%sArg", annotatedElement.getSimpleName());
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
        // 添加必须的参数
        for (Element e : required) {
            String paramName = getParamName(e);
            builder.addField(getTypeNameBox(e), paramName, Modifier.PRIVATE);

            createMethod.addParameter(createNonNullParam(e, paramName));
            createMethod.addStatement("builder.$N = $N", paramName, paramName);
        }
        createMethod.addStatement("return builder");
        builder.addMethod(createMethod.build());

        /**
         * 根据optional生成链式调用的方法
         */
        for (Element e : optional) {
            String paramName = getParamName(e);
            builder.addField(getTypeNameBox(e), paramName, Modifier.PRIVATE);
            builder.addMethod(MethodSpec.methodBuilder(paramName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(getTypeNameBox(e), paramName)
                    .addStatement("this.$N = $N", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.get(getPackageName(annotatedElement), name))
                    .build());
        }

        /**
         * 生成 build 方法
         */
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T b = new $T()", AndroidClassName.KBundle, AndroidClassName.KBundle);
        addIntentStatement(buildMethod, all);
        buildMethod.addStatement("$T frag = new $T()", annotatedTypeName, annotatedTypeName)
                .addStatement("frag.setArguments(b)")
                .addStatement("return frag")
                .returns(annotatedTypeName);
        builder.addMethod(buildMethod.build());

        /**
         * 生成host调用的inject方法
         */
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(annotatedTypeName, "frag")
                .addStatement("$T extras = frag.getArguments()", AndroidClassName.KBundle);
        for (Element e : all) {
            String paramName = getParamName(e);
            injectMethod.beginControlFlow("if (extras.containsKey($S))", paramName)
                    .addStatement("frag.$N = ($T) extras.get($S)", e.getSimpleName().toString(), e.asType(), paramName)
                    .nextControlFlow("else");

            Arg arg = e.getAnnotation(Arg.class);
            TypeName typeName = getTypeName(e);
            if (typeName == TypeName.LONG) {
                injectMethod.addStatement("frag.$N = " + arg.defaultLong(), e.getSimpleName().toString());
            } else if (typeName == TypeName.INT) {
                injectMethod.addStatement("frag.$N = " + arg.defaultInt(), e.getSimpleName().toString());
            } else if (typeName == TypeName.FLOAT) {
                injectMethod.addStatement("frag.$N = " + arg.defaultFloat(), e.getSimpleName().toString());
            } else if (typeName == TypeName.BOOLEAN) {
                injectMethod.addStatement("frag.$N = " + arg.defaultBoolean(), e.getSimpleName().toString());
            } else {
                // Object
                injectMethod.addStatement("frag.$N = null", e.getSimpleName().toString());
            }
            injectMethod.endControlFlow();
        }
        builder.addMethod(injectMethod.build());

        return builder.build();
    }

    private void getAnnotatedFields(Element annotatedElement, List<Element> required, List<Element> optional) {
        for (Element e : annotatedElement.getEnclosedElements()) {
            Arg a = e.getAnnotation(Arg.class);
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

    private String getParamName(Element e) {
        String val = e.getAnnotation(Arg.class).value();
        return getParamName(e, val);
    }

    private void addIntentStatement(MethodSpec.Builder builder, List<Element> all) {
        for (Element e : all) {
            String paramName = getParamName(e);
            builder.beginControlFlow("if ($N != null)", paramName);
            TypeName typeName = getTypeNameBox(e);
            if (typeName.isBoxedPrimitive()) {
                // Long Boolean Integer...
                if (typeName.unbox() == TypeName.INT) {
                    builder.addStatement("b.put$N($S, $N)", "Int", paramName, paramName);
                } else {
                    builder.addStatement("b.put$T($S, $N)", typeName, paramName, paramName);
                }
            } else {
                // 判断是否为String, serialize等, FIXME: 可以自行添加类型
                if (typeName.equals(JavaClassName.KString)) {
                    builder.addStatement("b.put$T($S, $N)", typeName, paramName, paramName);
                } else {
                    builder.addStatement("b.put$T($S, $N)", Serializable.class, paramName, paramName);
                }
            }

            builder.endControlFlow();
        }
    }
}