package yy.doctor.activity;

import org.json.JSONException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import lib.ys.model.EVal;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;
import yy.doctor.network.JsonParser;

/**
 * @auther yuansui
 * @since 2017/5/6
 */

public class EValFactory extends Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //进行条件判断，如果传进来的Type不是class，则匹配失败
        if (!(type instanceof Class<?>)) {
            return null;
        }

        //进行条件判断，如果传进来的Type不是EVal的实现类，则也匹配失败
        Class<?> c = (Class<?>) type;
        if (!EVal.class.isAssignableFrom(c)) {
            return null;
        }

        return new EValRespConverter(c);
    }

//    @Override
//    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//        //进行条件判断，如果传进来的Type不是class，则匹配失败
//        if (!(type instanceof Class<?>)) {
//            return null;
//        }
//        //进行条件判断，如果传进来的Type不是MessageLite的实现类，则也匹配失败
//        if (!EVal.class.isAssignableFrom((Class<?>) type)) {
//            return null;
//        }
//        return new EValRequestConverter<>();
//    }


    public class EValRespConverter implements Converter<ResponseBody, Object> {

        private Class mClz;

        public EValRespConverter(Class clz) {
            mClz = clz;
        }

        @Override
        public Object convert(ResponseBody responseBody) throws IOException {
            EValParser p = new JsonParser();
            try {
                return p.parse(responseBody.string(), mClz);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//    public class EValRequestConverter<T extends EVal> implements Converter<T, RequestBody> {
//
//        @Override
//        public RequestBody convert(T t) throws IOException {
//            return RequestBody.create(MediaType.parse("application/octet-stream"), t.toCommonJson());
//        }
//    }
}
