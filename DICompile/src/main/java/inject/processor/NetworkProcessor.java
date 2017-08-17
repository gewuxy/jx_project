package inject.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import inject.android.MyClassName;
import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.Key;
import inject.annotation.network.Path;
import inject.annotation.network.method.DOWNLOAD;
import inject.annotation.network.method.DOWNLOAD_FILE;
import inject.annotation.network.method.GET;
import inject.annotation.network.method.POST;
import inject.annotation.network.method.UPLOAD;

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
        String KNetworkBuilder = "builder";
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
                    List<VariableElement> required = new ArrayList<>();
                    List<VariableElement> optional = new ArrayList<>();
                    getAnnotatedFields(methodEle, required, optional);

                    String methodName = methodEle.getSimpleName().toString();

                    String methodClassName = apiName + "_" + methodName;
                    TypeSpec.Builder methodClzBuilder = TypeSpec.classBuilder(methodClassName)
                            .addModifiers(PUBLIC, FINAL, STATIC);

                    MethodSpec.Builder methodInstBuilder = MethodSpec.methodBuilder(methodName)
                            .addModifiers(PUBLIC, STATIC, FINAL)
                            .returns(ClassName.bestGuess(methodClassName))
                            .addStatement("$N inst = new $N($L)", methodClassName, methodClassName, getRequiredParamStatement(required))
                            .addStatement("return inst");

                    for (Element e : required) {
                        methodInstBuilder.addParameter(getTypeNameBox(e), e.getSimpleName().toString());
                    }

                    writeAPI(methodEle, methodClassName, apiName, name, methodClzBuilder);
                    apiBuilder.addMethod(methodInstBuilder.build());
                    apiBuilder.addType(methodClzBuilder.build());
                }

                builder.addType(apiBuilder.build());
            }
        }

        return builder.build();
    }

    private void writeAPI(Element ele, String methodName, String parentName, String baseName, TypeSpec.Builder typeBuilder) {
        List<VariableElement> required = new ArrayList<>();
        List<VariableElement> optional = new ArrayList<>();
        List<VariableElement> all = new ArrayList<>();

        getAnnotatedFields(ele, required, optional);
        all.addAll(required);
        all.addAll(optional);

        for (VariableElement e : all) {
            String paramName = e.getSimpleName().toString();
            typeBuilder.addField(getTypeNameBox(e), paramName, PRIVATE);
        }

        if (!required.isEmpty()) {
            MethodSpec.Builder b = MethodSpec.constructorBuilder()
                    .addModifiers(PUBLIC);

            for (VariableElement e : required) {
                String paramName = e.getSimpleName().toString();
                b.addParameter(createNonNullParam(e, paramName));
                b.addStatement("this.$N = $N", paramName, paramName);
            }

            typeBuilder.addMethod(b.build());
        }

        Path path = ele.getAnnotation(Path.class);
        String pathVal = path.value();

        typeBuilder.addField(FieldSpec.builder(MyClassName.KNetworkReqBuilder, FieldName.KNetworkBuilder, PRIVATE)
                .initializer(CodeBlock.of("$T.newBuilder($N + $S)",
                        MyClassName.KNetworkReq,
                        FieldName.KBaseHost,
                        pathVal))
                .build());

        MethodSpec.Builder b = MethodSpec.methodBuilder("build")
                .addModifiers(PUBLIC)
                .returns(MyClassName.KNetworkReq);

        if (ele.getAnnotation(GET.class) != null) {
            b.addStatement("$N.get()", FieldName.KNetworkBuilder);
        } else if (ele.getAnnotation(POST.class) != null) {
            b.addStatement("b.post()");
        } else if (ele.getAnnotation(UPLOAD.class) != null) {
            b.addStatement("b.upload()");
        } else if (ele.getAnnotation(DOWNLOAD.class) != null) {
            b.addStatement("b.download()");
        } else if (ele.getAnnotation(DOWNLOAD_FILE.class) != null) {
            DOWNLOAD_FILE df = ele.getAnnotation(DOWNLOAD_FILE.class);
            b.addStatement("b.downloadFile($S, $S)", df.dir(), df.fileName());
        }

        for (VariableElement e : optional) {
            String paramName = e.getSimpleName().toString();
            // 生成链式调用方法
            typeBuilder.addMethod(MethodSpec.methodBuilder(paramName)
                    .addModifiers(PUBLIC)
                    .addParameter(getTypeNameBox(e), paramName)
//                    .addStatement("$N.param($N, $L)", FieldName.KNetworkBuilder, paramName)
                    .addStatement("this.$N = $L", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.bestGuess(methodName))
                    .build());
        }

        for (VariableElement e : all) {
            b.addStatement("$N.param($S, $L)", FieldName.KNetworkBuilder, getParamName(e), e);
        }

        /**
         * 添加共用参数
         */
        b.addStatement("$N.param($T.getConfig().getCommonParams())", FieldName.KNetworkBuilder, MyClassName.KNetwork);
        b.addStatement("$N.header($T.getConfig().getCommonHeaders())", FieldName.KNetworkBuilder, MyClassName.KNetwork);

        b.addStatement("return $N.build()", FieldName.KNetworkBuilder);

        typeBuilder.addMethod(b.build());
    }

    private void getAnnotatedFields(Element ele, List<VariableElement> required, List<VariableElement> optional) {
        ExecutableElement executableElement = (ExecutableElement) ele;
        for (VariableElement e : executableElement.getParameters()) {
            Key a = e.getAnnotation(Key.class);
            if (a != null) {
                if (a.opt()) {
                    optional.add(e);
                } else {
                    required.add(e);
                }
            } else {
                required.add(e);
            }
        }
    }

    private String getParamName(Element ele) {
        Key key = ele.getAnnotation(Key.class);
        String name = ele.getSimpleName().toString();
        if (key != null) {
            return key.value().isEmpty() ? name : key.value();
        } else {
            return name;
        }
    }

    private StringBuffer getRequiredParamStatement(List<VariableElement> required) {
        StringBuffer statement = new StringBuffer();
        for (int i = 0; i < required.size(); ++i) {
            statement.append(required.get(i).getSimpleName());
            if (i != required.size() - 1) {
                statement.append(", ");
            }
        }
        return statement;
    }
}
