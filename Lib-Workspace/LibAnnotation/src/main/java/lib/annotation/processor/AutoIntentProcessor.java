package lib.annotation.processor;

/**
 * @auther yuansui
 * @since 2017/8/1
 */

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

import lib.annotation.AndroidClassName;
import lib.annotation.AutoIntent;
import lib.annotation.Extra;

@AutoService(Processor.class)
public class AutoIntentProcessor extends BaseProcessor {


    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return AutoIntent.class;
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
         * 生成new intent方法
         */
        MethodSpec.Builder newIntentMethod = MethodSpec.methodBuilder("newIntent")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
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

        if (annotatedTypeName.toString().toLowerCase().contains("serv")) {
            // 服务
            /**
             * 生成 start 方法
             */
            MethodSpec.Builder startMethod = MethodSpec.methodBuilder("start")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(createNonNullParam(AndroidClassName.KContext, "context"))
                    .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.KIntent, annotatedTypeName);
            addIntentStatement(startMethod, all);
            startMethod.addStatement("context.startService(intent)");
            builder.addMethod(startMethod.build());

            /**
             * 生成 stop 方法
             */
            MethodSpec.Builder stopMethod = MethodSpec.methodBuilder("stop")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(createNonNullParam(AndroidClassName.KContext, "context"))
                    .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.KIntent, annotatedTypeName)
                    .addStatement("context.stopService(intent)");
            builder.addMethod(stopMethod.build());
        } else {
            // activity
            /**
             * 生成 start 方法
             */
            MethodSpec.Builder startMethod = MethodSpec.methodBuilder("start")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(createNonNullParam(AndroidClassName.KContext, "context"))
                    .addStatement("$T intent = new Intent(context, $T.class)", AndroidClassName.KIntent, annotatedTypeName);
            addIntentStatement(startMethod, all);
            startMethod.beginControlFlow("if (!(context instanceof $T))", AndroidClassName.KActivity)
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
                    .beginControlFlow("if (!(host instanceof $T) || !(host instanceof $T))", AndroidClassName.KActivity, AndroidClassName.KFragment)
                    .addStatement("throw new $T(\"host must be one of activity or fragment\")", IllegalStateException.class)
                    .endControlFlow()
                    .addStatement("$T intent = new Intent((Context) host, $T.class)", AndroidClassName.KIntent, annotatedTypeName);
            addIntentStatement(startForResultMethod, all);
            startForResultMethod.beginControlFlow("if (host instanceof $T)", AndroidClassName.KActivity)
                    .addStatement("(($T) host).startActivityForResult(intent, code)", AndroidClassName.KActivity)
                    .nextControlFlow("else if (host instanceof $T)", AndroidClassName.KFragment)
                    .addStatement("(($T) host).startActivityForResult(intent, code)", AndroidClassName.KFragment)
                    .endControlFlow();
            builder.addMethod(startForResultMethod.build());
        }

        /**
         * 生成host调用的inject方法
         */
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(annotatedTypeName, "host")
                .addParameter(AndroidClassName.KIntent, "intent")
                .addStatement("$T extras = intent.getExtras()", AndroidClassName.KBundle);
        for (Element e : all) {
            String paramName = getParamName(e);
            injectMethod.beginControlFlow("if (extras.containsKey($S))", paramName)
                    .addStatement("host.$N = ($T) extras.get($S)", e.getSimpleName().toString(), e.asType(), paramName)
                    .nextControlFlow("else");

            Extra extra = e.getAnnotation(Extra.class);
            TypeName typeName = getTypeName(e);
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

    private String getParamName(Element e) {
        String extraVal = e.getAnnotation(Extra.class).value();
        return getParamName(e, extraVal);
    }

    private void addIntentStatement(MethodSpec.Builder builder, List<Element> all) {
        for (Element e : all) {
            String paramName = getParamName(e);
            builder.beginControlFlow("if ($N != null)", paramName)
                    .addStatement("intent.putExtra($S, $N)", paramName, paramName)
                    .endControlFlow();
        }
    }
}