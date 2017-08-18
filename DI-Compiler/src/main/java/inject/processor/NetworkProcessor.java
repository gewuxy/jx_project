package inject.processor;

import com.google.auto.service.AutoService;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import inject.android.MyClassName;
import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.Part;
import inject.annotation.network.Retry;
import inject.annotation.network.Url;
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
 * 和LibNetwork结合使用
 * 生成的都是NetworkReq
 *
 * @auther yuansui
 * @since 2017/8/16
 */
@AutoService(Processor.class)
public class NetworkProcessor extends BaseProcessor {

    private final String KSplit = ", ";

    private interface FieldName {
        String KHost = "KHost";
        String KHostDebuggable = "KHostDebuggable";
        String KBaseHost = "mBaseHost";
        String KBuilder = "builder";
        String KDir = "dir";
        String KFileName = "fileName";
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
        builder.addMethod(MethodSpec.methodBuilder("setDebuggable")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(TypeName.BOOLEAN, "state")
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

                    StringBuffer paramStatements = getRequiredParamStatement(required);
                    if (isDownloadFileType(methodEle)) {
                        // 下载文件的参数
                        if (paramStatements.length() != 0) {
                            paramStatements.append(KSplit);
                        }
                        paramStatements.append(FieldName.KDir)
                                .append(KSplit)
                                .append(FieldName.KFileName);
                    }

                    MethodSpec.Builder methodInstBuilder = MethodSpec.methodBuilder(methodName)
                            .addModifiers(PUBLIC, STATIC, FINAL)
                            .returns(ClassName.bestGuess(methodClassName))
                            .addStatement("$N inst = new $N($L)", methodClassName, methodClassName, paramStatements)
                            .addStatement("return inst");

                    if (isDownloadFileType(methodEle)) {
                        // 下载文件的参数
                        methodInstBuilder.addParameter(ParameterSpec.builder(String.class, FieldName.KDir).build());
                        methodInstBuilder.addParameter(ParameterSpec.builder(String.class, FieldName.KFileName).build());
                    }

                    for (Element e : required) {
                        methodInstBuilder.addParameter(getTypeName(e), e.getSimpleName().toString());
                    }

                    writeAPI(methodEle, methodClassName, methodClzBuilder, api);

                    apiBuilder.addMethod(methodInstBuilder.build());
                    apiBuilder.addType(methodClzBuilder.build());
                }

                builder.addType(apiBuilder.build());
            }
        }

        return builder.build();
    }

    private void writeAPI(Element ele, String methodName, TypeSpec.Builder typeBuilder, API api) {
        List<VariableElement> required = new ArrayList<>();
        List<VariableElement> optional = new ArrayList<>();
        List<VariableElement> all = new ArrayList<>();

        getAnnotatedFields(ele, required, optional);
        all.addAll(required);
        all.addAll(optional);

        // 添加fields
        for (VariableElement e : all) {
            String paramName = e.getSimpleName().toString();

            Url url = getAnnotation(e, Url.class);
            if (url == null) {
                typeBuilder.addField(getTypeName(e), paramName, PRIVATE);
            }
        }

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE);
        if (!required.isEmpty()) {
            for (VariableElement e : required) {
                String paramName = e.getSimpleName().toString();
                constructorBuilder.addParameter(createNonNullParam(e, paramName));

                Url url = getAnnotation(e, Url.class);
                if (url == null) {
                    constructorBuilder.addStatement("this.$N = $N", paramName, paramName);
                }
            }
        }

        String baseHost = FieldName.KBaseHost;
        String pathVal = null;

        MethodSpec.Builder b = MethodSpec.methodBuilder("build")
                .addModifiers(PUBLIC)
                .returns(MyClassName.KNetworkReq);

        /**
         * 是否有{@link Retry}
         */
        Retry retry = getAnnotation(ele, Retry.class);
        if (retry != null) {
            b.addStatement("$N.retry($L, $L)", FieldName.KBuilder, retry.count(), retry.delay());
        }

        if (isDownloadFileType(ele)) {
            // 下载文件多加入两个默认参数
            typeBuilder.addField(FieldSpec.builder(String.class, FieldName.KDir, PRIVATE).build());
            typeBuilder.addField(FieldSpec.builder(String.class, FieldName.KFileName, PRIVATE).build());

            constructorBuilder.addParameter(createNonNullParam(String.class, FieldName.KDir));
            constructorBuilder.addParameter(createNonNullParam(String.class, FieldName.KFileName));
            constructorBuilder.addStatement("this.$N = $L", FieldName.KDir, FieldName.KDir);
            constructorBuilder.addStatement("this.$N = $L", FieldName.KFileName, FieldName.KFileName);

            b.addStatement("$N.download($L, $L)", FieldName.KBuilder, FieldName.KDir, FieldName.KFileName);

            pathVal = getAnnotation(ele, DOWNLOAD_FILE.class).value();
        } else {
            if (getAnnotation(ele, GET.class) != null) {
                pathVal = getAnnotation(ele, GET.class).value();
                b.addStatement("$N.get()", FieldName.KBuilder);
            } else if (ele.getAnnotation(POST.class) != null) {
                pathVal = getAnnotation(ele, POST.class).value();
                b.addStatement("$N.post()", FieldName.KBuilder);
            } else if (ele.getAnnotation(UPLOAD.class) != null) {
                pathVal = getAnnotation(ele, UPLOAD.class).value();
                b.addStatement("$N.upload()", FieldName.KBuilder);
            } else if (ele.getAnnotation(DOWNLOAD.class) != null) {
                pathVal = getAnnotation(ele, DOWNLOAD.class).value();
                b.addStatement("$N.download()", FieldName.KBuilder);
            }
        }

        String urlName = null;
        // 如果有声明@Url, 则使用@Url的作为baseHost
        for (VariableElement e : required) {
            // @Url的声明只支持必需参数
            Url url = getAnnotation(e, Url.class);
            if (url != null) {
                urlName = e.getSimpleName().toString();
                break;
            }
        }

        if (urlName == null) {
            String apiVal = api.value();
            if (!apiVal.isEmpty()) {
                if (!apiVal.endsWith("/")) {
                    apiVal += "/";
                }
                constructorBuilder.addStatement("this.$N = $T.newBuilder($N + $S + $S)", FieldName.KBuilder, MyClassName.KNetworkReq, baseHost, apiVal, pathVal);
            } else {
                constructorBuilder.addStatement("this.$N = $T.newBuilder($N + $S)", FieldName.KBuilder, MyClassName.KNetworkReq, baseHost, pathVal);
            }
        } else {
            constructorBuilder.addStatement("this.$N = $T.newBuilder($L)", FieldName.KBuilder, MyClassName.KNetworkReq, urlName);
        }

        typeBuilder.addField(FieldSpec.builder(MyClassName.KNetworkReqBuilder, FieldName.KBuilder, PRIVATE)
                .build());

        for (VariableElement e : optional) {
            String paramName = e.getSimpleName().toString();
            // 生成链式调用方法
            typeBuilder.addMethod(MethodSpec.methodBuilder(paramName)
                    .addModifiers(PUBLIC)
                    .addParameter(getTypeName(e), paramName)
                    .addStatement("this.$N = $L", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.bestGuess(methodName))
                    .build());
        }

        for (VariableElement e : all) {
            if (!isDownloadFileType(ele)) {
                b.addStatement("$N.param($S, $L)", FieldName.KBuilder, getParamName(e), e);
            }
        }

        /**
         * 添加共用参数
         */
        b.addStatement("$N.param($T.getConfig().getCommonParams())", FieldName.KBuilder, MyClassName.KNetwork);
        b.addStatement("$N.header($T.getConfig().getCommonHeaders())", FieldName.KBuilder, MyClassName.KNetwork);

        b.addStatement("return $N.build()", FieldName.KBuilder);

        typeBuilder.addMethod(constructorBuilder.build());

        typeBuilder.addMethod(b.build());
    }

    private void getAnnotatedFields(Element ele, List<VariableElement> required, List<VariableElement> optional) {
        ExecutableElement executableElement = (ExecutableElement) ele;
        for (VariableElement e : executableElement.getParameters()) {
            Part part = e.getAnnotation(Part.class);
            if (part != null) {
                if (part.opt()) {
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
        Part part = ele.getAnnotation(Part.class);
        String name = ele.getSimpleName().toString();
        if (part != null) {
            return part.value().isEmpty() ? name : part.value();
        } else {
            return name;
        }
    }

    private StringBuffer getRequiredParamStatement(List<VariableElement> required) {
        StringBuffer statement = new StringBuffer();
        for (int i = 0; i < required.size(); ++i) {
            statement.append(required.get(i).getSimpleName());
            if (i != required.size() - 1) {
                statement.append(KSplit);
            }
        }
        return statement;
    }

    private boolean isDownloadFileType(Element e) {
        return e.getAnnotation(DOWNLOAD_FILE.class) != null;
    }

    private <T extends Annotation> T getAnnotation(Element ele, Class<T> clz) {
        return ele.getAnnotation(clz);
    }
}
