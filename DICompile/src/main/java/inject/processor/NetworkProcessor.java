package inject.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import inject.android.MyClassName;
import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.Key;
import inject.annotation.network.method.GET;
import inject.annotation.network.method.Path;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@AutoService(Processor.class)
public class NetworkProcessor extends BaseProcessor {

    private interface FieldName {
        String KHost = "KHost";
        String KHostDebuggable = "KHostDebuggable";
        String KBaseHost = "mBaseHost";
        String KNetworkBuilder = "b";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return APIFactory.class;
    }

    @Override
    protected TypeSpec getBuilderSpec(Element annotatedElement) {
        final String name = String.format("%sSetter", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
                .addModifiers(PUBLIC, FINAL);

        /**
         * 构造方法
         */
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE);
        builder.addMethod(constructor.build());

        /**
         * 寻找{@link APIFactory}
         */
        APIFactory apiFactory = annotatedElement.getAnnotation(APIFactory.class);
        if (apiFactory != null) {
            // 加入host field
            builder.addField(FieldSpec.builder(String.class, FieldName.KHost, PRIVATE, STATIC, FINAL)
                    .initializer(Format.KString, apiFactory.host())
                    .build());

            if (!apiFactory.hostDebuggable().isEmpty()) {
                builder.addField(FieldSpec.builder(String.class, FieldName.KHostDebuggable, PRIVATE, STATIC, FINAL)
                        .initializer(Format.KString, apiFactory.hostDebuggable())
                        .build());
            }

            builder.addField(FieldSpec.builder(String.class, FieldName.KBaseHost, PRIVATE, STATIC)
                    .initializer(Format.KVal, FieldName.KHostDebuggable)
                    .build());
        }

        /**
         * setDebuggable()
         */
//        builder.addField(FieldSpec.builder(Boolean.class, FieldName.KDebuggable, PRIVATE, STATIC)
//                .initializer(Format.KVal, true)
//                .build());
        builder.addMethod(MethodSpec.methodBuilder("setDebuggable")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(TypeName.BOOLEAN, "state")
//                .addStatement("$N = state", FieldName.KDebuggable)
                .beginControlFlow("if (state)")
                .addStatement("$N = $L", FieldName.KBaseHost, FieldName.KHostDebuggable)
                .nextControlFlow("else")
                .addStatement("$N = $L", FieldName.KBaseHost, FieldName.KHost)
                .endControlFlow()
                .build());

        /**
         * 寻找{@link API}
         */
        for (Element apiEle : annotatedElement.getEnclosedElements()) {
            API api = apiEle.getAnnotation(API.class);
            if (api != null && apiEle.getKind().isInterface()) {
                // 只支持interface
                // 生成对应API的class
                final String apiName = String.format("%sAPI", apiEle.getSimpleName());
                TypeSpec.Builder apiBuilder = TypeSpec.classBuilder(apiName)
                        .addModifiers(PUBLIC, STATIC, FINAL);

                for (Element methodEle : apiEle.getEnclosedElements()) {

                    Path path = methodEle.getAnnotation(Path.class);
                    String pathVal = path.value();
//                    if (!pathVal.endsWith("/")) {
//                        pathVal += "/";
//                    }

                    String methodName = methodEle.getSimpleName().toString();
                    MethodSpec.Builder b = MethodSpec.methodBuilder(methodName)
                            .addModifiers(PUBLIC, STATIC, FINAL)
                            .returns(MyClassName.KNetworkReq)
                            .addStatement("$T $N = $T.newBuilder($N + $S)",
                                    MyClassName.KNetworkReqBuilder,
                                    FieldName.KNetworkBuilder,
                                    MyClassName.KNetworkReq,
                                    FieldName.KBaseHost,
                                    pathVal
                            );

                    if (methodEle.getAnnotation(GET.class) != null) {
                        b.addStatement("b.get()");
                    } else if (methodEle.getAnnotation(GET.class) != null) {
                        b.addStatement("b.post()");
                    }

                    ExecutableElement executableElement = (ExecutableElement) methodEle;
                    for (VariableElement e : executableElement.getParameters()) {
                        String paramName = e.getSimpleName().toString();

                        Key key = e.getAnnotation(Key.class);
                        if (key != null) {
                            if (key.opt()) {
                                // 生成链式调用方法
                                apiBuilder.addField(getTypeNameBox(e), paramName, PRIVATE);

                                apiBuilder.addMethod(MethodSpec.methodBuilder(paramName)
                                        .addModifiers(PUBLIC)
                                        .addParameter(getTypeNameBox(e), paramName)
                                        .addStatement("this.$N = $N", paramName, paramName)
                                        .addStatement("return this")
                                        .returns(ClassName.get(getPackageName(annotatedElement) + "." + name, apiName))
                                        .build());
                            } else {
                                b.addParameter(getTypeName(e), paramName);
                                b.addStatement("$N.param($S, $L)", FieldName.KNetworkBuilder, key.value(), e);
                            }
                        } else {
                            b.addParameter(getTypeName(e), paramName);
                            b.addStatement("$N.param($S, $L)", FieldName.KNetworkBuilder, paramName, e);
                        }
                    }

                    /**
                     * 添加共用参数
                     */
                    b.addStatement("b.param($T.getConfig().getCommonParams())", MyClassName.KNetwork);
                    b.addStatement("b.header($T.getConfig().getCommonHeaders())", MyClassName.KNetwork);

                    b.addStatement("return $N.build()", FieldName.KNetworkBuilder);
                    apiBuilder.addMethod(b.build());
                }

                builder.addType(apiBuilder.build());
            }
        }

        return builder.build();
    }
}
