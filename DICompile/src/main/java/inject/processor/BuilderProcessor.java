package inject.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import inject.annotation.builder.Builder;
import inject.annotation.builder.Ignore;


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
         * 生成filed的设置方法
         */
        TypeElement classElement = (TypeElement) annotatedElement;
        String key = classElement.getQualifiedName().toString();

        TypeElement typeElement = getElementUtils().getTypeElement(key);
        List<? extends Element> allMembers = getElementUtils().getAllMembers(typeElement);
        if (allMembers.size() > 0) {
            List<VariableElement> fields = ElementFilter.fieldsIn(allMembers);
            for (VariableElement field : fields) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                String paramName = getParamName(field, field.getSimpleName().toString());

                FieldSpec.Builder fieldBuilder = FieldSpec.builder(getTypeName(field), paramName, Modifier.PRIVATE);
//                for (AnnotationMirror annotation : field.getAnnotationMirrors()) {
//                    getMessager().printMessage(Kind.NOTE, annotation.getAnnotationType().asElement().getSimpleName().toString());
//                    fieldBuilder.addAnnotation(ClassName.bestGuess(annotation.getAnnotationType().asElement().getSimpleName().toString()));
//                }
                builder.addField(fieldBuilder.build());

                MethodSpec spec = MethodSpec.methodBuilder(paramName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(getTypeName(field), paramName)
                        .addStatement("this.$N = $N", paramName, paramName)
                        .addStatement("return this")
                        .returns(clzName)
                        .build();

                builder.addMethod(spec);
            }
        }

        TypeName annotatedTypeName = getTypeName(annotatedElement);
        /**
         * 生成build方法
         */
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(annotatedElement.asType()))
                .addStatement("$T inst = new $T()", annotatedTypeName, annotatedElement);

        if (allMembers.size() > 0) {
            List<VariableElement> fields = ElementFilter.fieldsIn(allMembers);
            for (VariableElement field : fields) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                String filedName = field.getSimpleName().toString();
                String paramName = getParamName(field, filedName);

                TypeName typeName = getTypeName(field);
                if (typeName.isBoxedPrimitive()) {
                    // 基础类型Integer Long等
                    buildMethod.beginControlFlow("if (this.$N != null)", paramName);
                } else {
                    // 基础类型int long等
                    if (typeName == TypeName.LONG) {
                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
                    } else if (typeName == TypeName.INT) {
                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
                    } else if (typeName == TypeName.FLOAT) {
                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
                    } else if (typeName == TypeName.DOUBLE) {
                        buildMethod.beginControlFlow("if (this.$N != 0)", paramName);
                    } else if (typeName == TypeName.BOOLEAN) {
                        buildMethod.beginControlFlow("if (!this.$N)", paramName);
                    } else {
                        buildMethod.beginControlFlow("if (this.$N != null)", paramName);
                    }
                }

                buildMethod.addStatement("inst.$N = $N", filedName, paramName)
                        .endControlFlow();
            }
        }
        buildMethod.addStatement("return inst");
        builder.addMethod(buildMethod.build());

        return builder.build();
    }
}
