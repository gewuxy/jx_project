package lib.annotation.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import lib.annotation.AutoBuilder;
import lib.annotation.Extra;
import lib.annotation.Ignore;

import static java.nio.file.Paths.get;


/**
 * @auther yuansui
 * @since 2017/8/2
 */

@AutoService(Processor.class)
public class AutoBuilderProcessor extends BaseProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return AutoBuilder.class;
    }

    @Override
    protected TypeSpec getBuilderSpec(Element annotatedElement) {
        final String name = String.format("%sBuilder", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

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

                FieldSpec.Builder fieldBuilder = FieldSpec.builder(TypeName.get(field.asType()), paramName, Modifier.PRIVATE);
//                for (AnnotationMirror annotation : field.getAnnotationMirrors()) {
//                    getMessager().printMessage(Kind.NOTE, annotation.getAnnotationType().asElement().getSimpleName().toString());
//                    fieldBuilder.addAnnotation(ClassName.bestGuess(annotation.getAnnotationType().asElement().getSimpleName().toString()));
//                }
                builder.addField(fieldBuilder.build());

                MethodSpec spec = MethodSpec.methodBuilder(paramName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.get(field.asType()), paramName)
                        .addStatement("this.$N = $N", paramName, paramName)
                        .addStatement("return this")
                        .returns(ClassName.get(getPackageName(annotatedElement), name))
                        .build();

                builder.addMethod(spec);
            }
        }

        TypeName annotatedTypeName = TypeName.get(annotatedElement.asType());
        /**
         * 生成build方法
         */
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(annotatedElement.asType()))
                .addStatement("$T inst = new $T()", annotatedTypeName, annotatedElement);

        if (allMembers.size() > 0) {
            buildMethod.beginControlFlow("try");
            List<VariableElement> fields = ElementFilter.fieldsIn(allMembers);
            for (VariableElement field : fields) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                String filedName = field.getSimpleName().toString();
                String paramName = getParamName(field, filedName);

                TypeName typeName = TypeName.get(field.asType());
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

                buildMethod.addStatement("$T $N = $T.class.getDeclaredField(\"$N\")", Field.class, paramName, annotatedTypeName, filedName)
                        .addStatement("$N.setAccessible(true)", paramName)
                        .addStatement("$N.set(inst, this.$N)", paramName, paramName)
                        .endControlFlow();
            }

            buildMethod.nextControlFlow("catch (Exception e)")
                    .addStatement("e.printStackTrace()")
                    .endControlFlow();
        }
        buildMethod.addStatement("return inst");
        builder.addMethod(buildMethod.build());

        return builder.build();
    }
}
