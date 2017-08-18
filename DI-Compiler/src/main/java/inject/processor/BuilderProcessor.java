package inject.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import inject.annotation.builder.Builder;
import inject.annotation.builder.Ignore;
import inject.annotation.router.Arg;
import inject.annotation.router.Route;


/**
 * 用来生成简易的builder模式, 如果需要复杂的判断或者重载更多的方法, 需要自己写builder
 *
 * @auther yuansui
 * @since 2017/8/2
 */
@AutoService(Processor.class)
public class BuilderProcessor extends BaseProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Builder.class;
    }

    @Override
    protected TypeSpec getBuilderSpec(Element annotatedElement) {
        final String name = String.format("%sBuilder", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        /**
         * 生成构造方法
         */
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        builder.addMethod(constructor.build());

        /**
         * 生成create方法
         */
        ClassName clzName = ClassName.get(getPackageName(annotatedElement), name);
        String simpleName = clzName.simpleName();
        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement(simpleName + " builder = new " + simpleName + "()")
                .returns(clzName);
        createMethod.addStatement("return builder");
        builder.addMethod(createMethod.build());

        /**
         * 获取所有filed
         */
        TypeElement classElement = (TypeElement) annotatedElement;
        String key = classElement.getQualifiedName().toString();
        TypeElement typeElement = getElementUtils().getTypeElement(key);
        List<? extends Element> members = getElementUtils().getAllMembers(typeElement);
        List<VariableElement> fields = ElementFilter.fieldsIn(members);

        /**
         * 生成filed的设置方法
         */
        for (VariableElement field : fields) {
            if (field.getAnnotation(Ignore.class) != null) {
                continue;
            }

            String paramName = getParamName(field, field.getSimpleName().toString());

            // 添加成员变量
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(getTypeNameBox(field), paramName, Modifier.PRIVATE);
            fieldBuilder.addAnnotations(getAnnotations(field));
            builder.addField(fieldBuilder.build());

            // 添加set方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(paramName)
                    .addModifiers(Modifier.PUBLIC);

            ParameterSpec.Builder paramBuilder = ParameterSpec.builder(getTypeName(field), paramName);
            paramBuilder.addAnnotations(getAnnotations(field));

            methodBuilder.addParameter(paramBuilder.build())
                    .addStatement("this.$N = $N", paramName, paramName)
                    .addStatement("return this")
                    .returns(clzName);

            builder.addMethod(methodBuilder.build());
        }

        TypeName annotatedTypeName = getTypeName(annotatedElement);
        /**
         * 生成build方法
         */
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(annotatedElement.asType()))
                .addStatement("$T inst = new $T()", annotatedTypeName, annotatedElement);
        if (!fields.isEmpty()) {
            for (VariableElement field : fields) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                String filedName = field.getSimpleName().toString();
                String paramName = getParamName(field, filedName);

                buildMethod.beginControlFlow("if (this.$N != null)", paramName);
//                TypeName typeName = getTypeName(field);
//                if (typeName.isBoxedPrimitive()) {
//                    // 基础类型Integer Long等
//                    buildMethod.beginControlFlow("if (this.$N != null)", paramName);
//                } else {
//                    // 基础类型int long等
//                    if (typeName == TypeName.LONG) {
//                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
//                    } else if (typeName == TypeName.INT) {
//                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
//                    } else if (typeName == TypeName.FLOAT) {
//                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
//                    } else if (typeName == TypeName.DOUBLE) {
//                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
//                    } else if (typeName == TypeName.BOOLEAN) {
//                        buildMethod.beginControlFlow("if (!this.$N)", paramName);
//                    } else {
//                        buildMethod.beginControlFlow("if (this.$N != null)", paramName);
//                    }
//                }
                buildMethod.addStatement("inst.$N = $N", filedName, paramName)
                        .endControlFlow();
            }
        }
        buildMethod.addStatement("return inst");
        builder.addMethod(buildMethod.build());

        return builder.build();
    }

    /**
     * 获取变量上的注解, 排除掉以下注解
     * {@link Arg}
     * {@link Route}
     *
     * @param field
     * @return
     */
    private List<AnnotationSpec> getAnnotations(VariableElement field) {
        List<AnnotationSpec> specs = new ArrayList<>();
        for (AnnotationMirror annotation : field.getAnnotationMirrors()) {
            AnnotationSpec spec = AnnotationSpec.get(annotation);
            String name = spec.toString();
            if (name.contains(Arg.class.getCanonicalName())) {
                continue;
            } else if (name.contains(Route.class.getCanonicalName())) {
                continue;
            }

            specs.add(AnnotationSpec.get(annotation));
        }
        return specs;
    }
}
