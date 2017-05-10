package lib.ys.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 泛型工具类
 *
 * @author yuansui
 */
public class GenericUtil {

    /**
     * 寻找指定的泛型类
     *
     * @param clz
     * @param destClz
     * @return
     */
    public static Class getClassType(Class clz, Class destClz) {
        Type genType = null;

        while (clz.getSuperclass() != null) {
            genType = clz.getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                clz = clz.getSuperclass();
                continue;
            } else {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                for (Type t : params) {
                    Class tClz = (Class) t;
                    if (destClz.isAssignableFrom(tClz)) {
                        return (Class) t;
                    } else {
                        continue;
                    }
                }
                break;
            }
        }

        return null;
    }

    /**
     * 通过反射,获得指定类的父类的泛型参数的实际类型. 如BuyerServiceBean extends DaoSupport<Buyer>
     *
     * @param clz   clazz 需要反射的类,该类必须继承范型父类
     * @param index 泛型参数所在索引,从0开始.
     * @return 范型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
     */
    public static Class getClassType(Class clz, int index) {
        Type genType = null;
        // 有可能子类的上级并不是泛型的直接子类, 那么需要找到最顶层的泛型父类
        while (clz.getSuperclass() != null) {
            genType = clz.getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                clz = clz.getSuperclass();
                continue;
            } else {
                break;
            }
        }

        if (clz.equals(Object.class)) {
            return clz;
        }

        //返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class, 如BuyerServiceBean extends DaoSupport<Buyer,Contact>就返回Buyer和Contact类型
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 通过反射,获得指定类的父类的第一个泛型参数的实际类型. 如BuyerServiceBean extends DaoSupport<Buyer>
     *
     * @param clz 需要反射的类,该类必须继承泛型父类
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
     */
    public static Class getClassType(Class clz) {
        return getClassType(clz, 0);
    }

    /**
     * 通过反射,获得方法返回值泛型参数的实际类型. 如: public Map<String, Buyer> getNames(){}
     *
     * @param method 方法
     * @param index  泛型参数所在索引,从0开始.
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
     */
    public static Class getMethodReturnType(Method method, int index) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            if (index >= typeArguments.length || index < 0) {
                throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
            }
            return (Class) typeArguments[index];
        }
        return Object.class;
    }

    /**
     * 通过反射,获得方法返回值第一个泛型参数的实际类型. 如: public Map<String, Buyer> getNames(){}
     *
     * @param method 方法
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
     */
    public static Class getMethodReturnType(Method method) {
        return getMethodReturnType(method, 0);
    }

    /**
     * 通过反射,获得方法输入参数第index个输入参数的所有泛型参数的实际类型. 如: public void add(Map<String, Buyer> maps, List<String> names){}
     *
     * @param method 方法
     * @param index  第几个输入参数
     * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回空集合
     */
    public static List<Class> getMethodParameterTypes(Method method, int index) {
        List<Class> results = new ArrayList<>();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (index >= genericParameterTypes.length || index < 0) {
            throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
        }
        Type genericParameterType = genericParameterTypes[index];
        if (genericParameterType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericParameterType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            for (Type parameterArgType : parameterArgTypes) {
                Class parameterArgClass = (Class) parameterArgType;
                results.add(parameterArgClass);
            }
            return results;
        }
        return results;
    }

    /**
     * 通过反射,获得方法输入参数第一个输入参数的所有泛型参数的实际类型. 如: public void add(Map<String, Buyer> maps, List<String> names){}
     *
     * @param method 方法
     * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回空集合
     */

    public static List<Class> getMethodParameterTypes(Method method) {
        return getMethodParameterTypes(method, 0);
    }

    /**
     * 通过反射,获得Field泛型参数的实际类型. 如: public Map<String, Buyer> names;
     *
     * @param field 字段
     * @param index 泛型参数所在索引,从0开始.
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
     */
    public static Class getFieldType(Field field, int index) {
        Type genericFieldType = field.getGenericType();

        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            if (index >= fieldArgTypes.length || index < 0) {
                throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
            }
            return (Class) fieldArgTypes[index];
        }
        return Object.class;
    }

    /**
     * 通过反射,获得Field泛型参数的实际类型. 如: public Map<String, Buyer> names;
     *
     * @param field 字段
     * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
     */
    public static Class getFieldType(Field field) {
        return getFieldType(field, 0);
    }
}
