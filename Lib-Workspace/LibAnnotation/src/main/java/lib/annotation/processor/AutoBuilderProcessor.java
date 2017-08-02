package lib.annotation.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import lib.annotation.AutoBuilder;


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
                String paramName = getParamName(field, field.getSimpleName().toString());
                builder.addField(TypeName.get(field.asType()), paramName, Modifier.PRIVATE);

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
                String filedName = field.getSimpleName().toString();
                String paramName = getParamName(field, filedName);

                buildMethod.addStatement("$T $N = $T.class.getDeclaredField(\"$N\")", Field.class, paramName, annotatedTypeName, filedName)
                        .addStatement("$N.setAccessible(true)", paramName)
                        .addStatement("$N.set(inst, this.$N)", paramName, paramName);
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
