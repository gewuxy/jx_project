package inject.processor;

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
import javax.lang.model.type.TypeMirror;

import inject.android.AndroidClassName;
import inject.android.AndroidName;
import inject.android.JavaClassName;
import inject.annotation.router.Arg;
import inject.annotation.router.Route;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @auther yuansui
 * @since 2017/8/14
 */
@AutoService(Processor.class)
public class RouterProcessor extends BaseProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Route.class;
    }

    @Override
    protected TypeSpec getBuilderSpec(Element annotatedElement) {
        TypeName annotatedTypeName = getTypeName(annotatedElement);
        TypeMirror annotatedMirror = annotatedElement.asType();

        TypeMirror activityTm = getElementUtils().getTypeElement(AndroidName.KActivity).asType();
        TypeMirror serviceTm = getElementUtils().getTypeElement(AndroidName.KService).asType();
        TypeMirror fragTm = getElementUtils().getTypeElement(AndroidName.KFragment_v4).asType();

        List<Element> required = new ArrayList<>();
        List<Element> optional = new ArrayList<>();
        List<Element> all = new ArrayList<>();

        getAnnotatedFields(annotatedElement, required, optional);
        all.addAll(required);
        all.addAll(optional);

        final String name = String.format("%sRouter", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                .addModifiers(PUBLIC, FINAL);

        /**
         * 生成构造方法
         */
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE);
        builder.addMethod(constructor.build());

        ClassName clzName = ClassName.get(getPackageName(annotatedElement), name);

        /**
         * 生成create方法
         */
        String simpleName = clzName.simpleName();
        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create")
                .addModifiers(PUBLIC, STATIC)
                .addStatement(simpleName + " builder = new " + simpleName + "()")
                .returns(clzName);
        // 添加必须的参数
        for (Element e : required) {
            String paramName = getParamName(e);
            builder.addField(getTypeNameBox(e), paramName, PRIVATE);

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
            builder.addField(getTypeNameBox(e), paramName, PRIVATE);

            builder.addMethod(MethodSpec.methodBuilder(paramName)
                    .addModifiers(PUBLIC)
                    .addParameter(getTypeNameBox(e), paramName)
                    .addStatement("this.$N = $N", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.get(getPackageName(annotatedElement), name))
                    .build());
        }

        /**
         * 生成new intent方法
         */
        if (isSubtype(annotatedMirror, serviceTm)
                || isSubtype(annotatedMirror, activityTm)) {
            // 只有activity和service需要
            MethodSpec.Builder newIntentMethod = MethodSpec.methodBuilder("newIntent")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(createNonNullParam(AndroidClassName.KContext, "context"))
                    .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.KIntent, annotatedTypeName)
                    .returns(AndroidClassName.KIntent);
            for (Element e : all) {
                String paramName = getParamName(e);
                newIntentMethod.addParameter(createNonNullParam(e, paramName));
            }
            addIntentStatement(newIntentMethod, all);
            newIntentMethod.addStatement("return intent");
            builder.addMethod(newIntentMethod.build());
        }

        if (isSubtype(annotatedMirror, serviceTm)) {
            // 服务
            /**
             * 生成 start 方法
             */
            MethodSpec.Builder startMethod = MethodSpec.methodBuilder("route")
                    .addModifiers(PUBLIC)
                    .addParameter(createNonNullParam(AndroidClassName.KContext, "context"))
                    .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.KIntent, annotatedTypeName);
            addIntentStatement(startMethod, all);
            startMethod.addStatement("context.startService(intent)");
            builder.addMethod(startMethod.build());

            /**
             * 生成 stop 方法
             */
            MethodSpec.Builder stopMethod = MethodSpec.methodBuilder("stop")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(createNonNullParam(AndroidClassName.KContext, "context"))
                    .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.KIntent, annotatedTypeName)
                    .addStatement("context.stopService(intent)");
            builder.addMethod(stopMethod.build());

        } else if (isSubtype(annotatedMirror, fragTm)) {
            // fragment
            MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("route")
                    .addModifiers(PUBLIC)
                    .addStatement("$T b = new $T()", AndroidClassName.KBundle, AndroidClassName.KBundle);
            addBundleStatement(buildMethod, all);
            buildMethod.addStatement("$T frag = new $T()", annotatedTypeName, annotatedTypeName)
                    .addStatement("frag.setArguments(b)")
                    .addStatement("return frag")
                    .returns(annotatedTypeName);
            builder.addMethod(buildMethod.build());
        } else if (isSubtype(annotatedMirror, activityTm)) {
            // activity
            /**
             * 生成 start 方法
             */
            MethodSpec.Builder startMethod = MethodSpec.methodBuilder("route")
                    .addModifiers(PUBLIC)
                    .addParameter(createNonNullParam(AndroidClassName.KContext, "context"))
                    .addStatement("$T intent = new $T(context, $T.class)", AndroidClassName.KIntent, AndroidClassName.KIntent, annotatedTypeName);
            addIntentStatement(startMethod, all);
            startMethod.beginControlFlow("if (!(context instanceof $T))", AndroidClassName.KActivity)
                    .addStatement("intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)")
                    .endControlFlow()
                    .addStatement("context.startActivity(intent)");
            builder.addMethod(startMethod.build());

            /**
             * 生成 start for result 方法
             */
            MethodSpec.Builder startForResultMethod = MethodSpec.methodBuilder("route")
                    .addModifiers(PUBLIC)
                    .addParameter(Object.class, "objectHost")
                    .addParameter(TypeName.INT, "code")
                    .beginControlFlow("if (!(objectHost instanceof $T) && !(objectHost instanceof $T))", AndroidClassName.KActivity, AndroidClassName.KFragment)
                    .addStatement("throw new $T(\"objectHost must be one of activity or fragment\")", IllegalStateException.class)
                    .endControlFlow()
                    .addStatement("$T intent = new Intent((Context) objectHost, $T.class)", AndroidClassName.KIntent, annotatedTypeName);
            addIntentStatement(startForResultMethod, all);
            startForResultMethod.beginControlFlow("if (objectHost instanceof $T)", AndroidClassName.KActivity)
                    .addStatement("(($T) objectHost).startActivityForResult(intent, code)", AndroidClassName.KActivity)
                    .nextControlFlow("else if (objectHost instanceof $T)", AndroidClassName.KFragment)
                    .addStatement("(($T) objectHost).startActivityForResult(intent, code)", AndroidClassName.KFragment)
                    .endControlFlow();
            builder.addMethod(startForResultMethod.build());
        }

        /**
         * 生成objectHost调用的inject方法
         */
        MethodSpec.Builder injectMethod = null;
        if (isSubtype(annotatedMirror, fragTm)) {
            injectMethod = MethodSpec.methodBuilder("inject")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(annotatedTypeName, "objectHost")
                    .addStatement("$T extras = objectHost.getArguments()", AndroidClassName.KBundle);
        } else {
            injectMethod = MethodSpec.methodBuilder("inject")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(annotatedTypeName, "objectHost")
                    .addParameter(AndroidClassName.KIntent, "intent")
                    .addStatement("$T extras = intent.getExtras()", AndroidClassName.KBundle);
        }

        for (Element e : all) {
            String paramName = getParamName(e);
            injectMethod.beginControlFlow("if (extras.containsKey($S))", paramName)
                    .addStatement("objectHost.$N = ($T) extras.get($S)", e.getSimpleName().toString(), e.asType(), paramName)
                    .nextControlFlow("else");

            Arg arg = e.getAnnotation(Arg.class);
            TypeName typeName = getTypeName(e);
            if (typeName == TypeName.LONG) {
                injectMethod.addStatement("objectHost.$N = " + arg.defaultLong(), e.getSimpleName().toString());
            } else if (typeName == TypeName.INT) {
                injectMethod.addStatement("objectHost.$N = " + arg.defaultInt(), e.getSimpleName().toString());
            } else if (typeName == TypeName.FLOAT) {
                injectMethod.addStatement("objectHost.$N = " + arg.defaultFloat(), e.getSimpleName().toString());
            } else if (typeName == TypeName.BOOLEAN) {
                injectMethod.addStatement("objectHost.$N = " + arg.defaultBoolean(), e.getSimpleName().toString());
            } else {
                // Object
                injectMethod.addStatement("objectHost.$N = null", e.getSimpleName().toString());
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
//                    .addModifiers(PUBLIC, STATIC)
//                    .addParameter(AndroidClassName.KIntent, "intent")
//                    .addStatement("$T extras = intent.getExtras()", AndroidClassName.KBundle)
//                    .beginControlFlow("if (extras.containsKey($S))", paramName)
//                    .addStatement("return ($T) extras.get($S)", e.asType(), paramName)
//                    .nextControlFlow("else")
//                    .addStatement("return null")
//                    .endControlFlow();
//            builder.addMethod(getterMethod.build());
//        }

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
        String extraVal = e.getAnnotation(Arg.class).value();
        return getParamName(e, extraVal);
    }

    private void addBundleStatement(MethodSpec.Builder builder, List<Element> elements) {
        for (Element e : elements) {
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

    private void addIntentStatement(MethodSpec.Builder builder, List<Element> elements) {
        for (Element e : elements) {
            String paramName = getParamName(e);
            builder.beginControlFlow("if ($N != null)", paramName)
                    .addStatement("intent.putExtra($S, $N)", paramName, paramName)
                    .endControlFlow();
        }
    }
}
