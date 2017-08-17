package inject.processor;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import inject.android.AnnotationClassName;

/**
 * @auther yuansui
 * @since 2017/7/28
 */

abstract public class BaseProcessor extends AbstractProcessor {

    protected interface Format {
        String KString = "$S";
        String KName = "$N";
        String KType = "$T";
        String KVal = "$L";
    }

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element annotatedElement : env.getElementsAnnotatedWith(getAnnotationClass())) {
            try {
                TypeSpec builderSpec = getBuilderSpec(annotatedElement);
                if (builderSpec != null) {
                    JavaFile builderFile = JavaFile.builder(getPackageName(annotatedElement), builderSpec).build();
                    builderFile.writeTo(filer);
                }
            } catch (Exception e) {
                printErr(annotatedElement, "Could not create builder for %s: %s", annotatedElement.getSimpleName(), e.getMessage());
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(getAnnotationClass().getCanonicalName());
    }

    abstract protected Class<? extends Annotation> getAnnotationClass();

    abstract protected TypeSpec getBuilderSpec(Element annotatedElement);

    protected Elements getElementUtils() {
        return elementUtils;
    }

    protected Types getTypeUtils() {
        return typeUtils;
    }

    protected Filer getFiler() {
        return filer;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 获取是否有声明的注解
     *
     * @param e
     * @param name 注解名字
     * @return
     */
    protected boolean hasAnnotation(Element e, String name) {
        for (AnnotationMirror annotation : e.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected String getPackageName(Element e) {
        while (!(e instanceof PackageElement)) {
            e = e.getEnclosingElement();
        }
        return ((PackageElement) e).getQualifiedName().toString();
    }

    protected <A extends Annotation> String getParamName(Element e, String val) {
        String ret = val != null && !val.trim().isEmpty() ? val : e.getSimpleName().toString();
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

    protected ParameterSpec createNonNullParam(Element e, String name) {
        TypeName typeName = getTypeNameBox(e);
        ParameterSpec.Builder builder = ParameterSpec.builder(typeName, name);
        builder.addAnnotation(AnnotationClassName.KNonNull);
        return builder.build();
    }

    protected ParameterSpec createNonNullParam(ClassName className, String name) {
        ParameterSpec.Builder builder = ParameterSpec.builder(className, name);
        builder.addAnnotation(AnnotationClassName.KNonNull);
        return builder.build();
    }

    protected ParameterSpec createNonNullParam(Type typeName, String name) {
        ParameterSpec.Builder builder = ParameterSpec.builder(typeName, name);
        builder.addAnnotation(AnnotationClassName.KNonNull);
        return builder.build();
    }

    /**
     * 获取类型名称(向上转型)
     *
     * @param e
     * @return
     */
    protected TypeName getTypeNameBox(Element e) {
        return TypeName.get(e.asType()).box();
    }

    /**
     * 获取类型名称
     *
     * @param e
     * @return
     */
    protected TypeName getTypeName(Element e) {
        return TypeName.get(e.asType());
    }

    protected boolean isSubtype(TypeMirror var1, TypeMirror var2) {
        return typeUtils.isSubtype(var1, var2);
    }

    protected void print(String message) {
        messager.printMessage(Kind.NOTE, message);
    }

    protected void printErr(String message) {
        messager.printMessage(Kind.ERROR, message);
    }

    protected void printErr(Element e, String msg, Object... args) {
        messager.printMessage(Kind.ERROR, String.format(msg, args), e);
    }
}
