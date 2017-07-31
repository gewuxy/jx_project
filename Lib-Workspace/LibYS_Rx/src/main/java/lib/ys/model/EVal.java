package lib.ys.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.util.GenericUtil;
import lib.ys.util.JsonUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.TextUtil;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 使用枚举的hash map, Key是任意枚举
 *
 * @param <E> 任意枚举
 * @author yuansui
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
abstract public class EVal<E extends Enum<E>> implements Serializable, Cloneable {

    protected String TAG = getClass().getSimpleName();

    /**
     * 初始化赋值
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    protected @interface Init {
        int asInt() default Integer.MIN_VALUE;

        long asLong() default Long.MIN_VALUE;

        float asFloat() default Float.MIN_VALUE;

        String asString() default ConstantsEx.KEmptyValue;
    }

    /**
     * 指定绑定的非基础数据类型(目前默认只支持{@link EVal}的类型)
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    protected @interface Bind {
        /**
         * 对象类
         *
         * @return
         */
        Class<? extends EVal> value() default EVal.class;

        /**
         * 绑定为list类型
         *
         * @return
         */
        Class<?> asList() default void.class;
    }

    private interface BooleanKey {
        String KTrue = "true";
        String KFalse = "false";
        String KNumTrue = "1";
        String KNumFalse = "0";
    }

    private Map<E, Object> mMap = null;

    public EVal() {
        mMap = new HashMap<>();

        Class clz = GenericUtil.getClassType(getClass());
        Field[] fields = clz.getFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Init.class)) {
                // 有初始化字段
                E e = (E) E.valueOf(clz, f.getName());
                Init annotation = f.getAnnotation(Init.class);
                int asInt = annotation.asInt();
                if (asInt != Integer.MIN_VALUE) {
                    put(e, asInt);
                    continue;
                }

                long asLong = annotation.asLong();
                if (asLong != Long.MIN_VALUE) {
                    put(e, asLong);
                    continue;
                }

                float asFloat = annotation.asFloat();
                if (asFloat != Float.MIN_VALUE) {
                    put(e, asFloat);
                    continue;
                }

                String asString = annotation.asString();
                if (TextUtil.isNotEmpty(asString)) {
                    put(e, asString);
                    continue;
                }
            }
        }
    }

    public final <T extends EVal<E>> T put(E key, Object o) {
        if (o == null) {
            return (T) this;
        }

        mMap.put(key, o);
        return (T) this;
    }

    public final <T extends EVal<E>> T put(T o) {
        if (o == null) {
            return (T) this;
        }

        // 过滤掉null
        Observable.fromIterable(getEnumFields())
                .filter(e -> o.getObject(e) != null)
                .subscribe(e -> put(e, o.getObject(e)));
        return (T) this;
    }

    public final void clear() {
        mMap.clear();
    }

    public Integer getInteger(E key) {
        return getInt(key);
    }

    public int getInt(E key) {
        return getInt(key, ConstantsEx.KInvalidValue);
    }

    public int getInt(E key, int defaultValue) {
        int v = defaultValue;
        Object o = getObject(key);
        if (o == null) {
            return v;
        }

        // 判断顺序很重要, string的需要放到最后
        try {
            if (o instanceof Integer) {
                v = (int) o;
            } else if (o instanceof Long) {
                v = ((Long) o).intValue();
            } else if (o instanceof Float) {
                v = ((Float) o).intValue();
            } else if (o instanceof Double) {
                v = ((Double) o).intValue();
            } else {
                // String类型或者辨认不出来类型, 直接用String接完以后转int
                NumberFormat nf = NumberFormat.getIntegerInstance();
                Number num = nf.parse(getString(key));
                v = num.intValue();
            }
        } catch (Exception e) {
            // LogMgr.e(TAG, e);
        }

        return v;
    }

    public String getString(E key) {
        return getString(key, ConstantsEx.KEmptyValue);
    }

    public String getString(E key, String defaultValue) {
        String v = defaultValue;
        Object o = getObject(key);
        if (o == null) {
            return v;
        }

        // 不用判断类型
        return String.valueOf(o);
    }

    public long getLong(E key) {
        return getLong(key, ConstantsEx.KInvalidValue);
    }

    public long getLong(E key, long defaultValue) {
        long v = defaultValue;
        Object o = getObject(key);
        if (o == null) {
            return v;
        }

        try {
            if (o instanceof Long) {
                v = (long) o;
            } else if (o instanceof Integer) {
                v = Long.valueOf((Integer) o);
            } else if (o instanceof String) {
                v = Long.valueOf((String) o);
            }
        } catch (NumberFormatException e) {
            // LogMgr.e(TAG, e);
        }

        return v;
    }

    public boolean getBoolean(E key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(E key, boolean defaultValue) {
        boolean v = defaultValue;
        Object o = getObject(key);
        if (o == null) {
            return v;
        }

        try {
            if (o instanceof Boolean) {
                v = (boolean) o;
            } else if (o instanceof Integer) {
                // 大部分情况都不会进这里
                // 数字规则, 0为false, 1为true
                Integer i = (Integer) o;
                if (i == 0) {
                    v = false;
                } else if (i == 1) {
                    v = true;
                } else {
                    v = false;
                }
            } else if (o instanceof String) {
                String str = String.valueOf(o);
                String val = str.toLowerCase();
                if (val.equals(BooleanKey.KTrue) || val.equals(BooleanKey.KNumTrue)) {
                    return true;
                } else if (val.equals(BooleanKey.KFalse) || val.equals(BooleanKey.KNumFalse)) {
                    return false;
                } else {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            // LogMgr.e(TAG, e);
        }

        return v;
    }

    public float getFloat(E key) {
        return getFloat(key, ConstantsEx.KInvalidValue);
    }

    public float getFloat(E key, float defaultValue) {
        float v = defaultValue;
        Object o = getObject(key);
        if (o == null) {
            return v;
        }

        try {
            if (o instanceof Float) {
                v = (float) o;
            } else if (o instanceof String) {
                v = Float.valueOf(getString(key));
            } else if (o instanceof Integer) {
                int i = (int) o;
                v = i;
            } else {
                // 检测不出类型(较少见)
                String s = String.valueOf(o);
                float f = Float.valueOf(s);
                v = f;
            }
        } catch (NumberFormatException e) {
            // LogMgr.e(TAG, e);
        }

        return v;
    }

    public Double getDouble(E key) {
        return getDouble(key, ConstantsEx.KInvalidValue);
    }

    public Double getDouble(E key, double defaultValue) {
        Double v = defaultValue;
        Object o = getObject(key);
        if (o == null) {
            return v;
        }

        try {
            if (o instanceof Double) {
                v = (Double) o;
            } else if (o instanceof String || o instanceof Integer) {
                v = Double.valueOf(getString(key));
            } else if (o instanceof Float) {
                v = (Double) o;
            }
        } catch (NumberFormatException e) {
            // LogMgr.e(TAG, e);
        }

        return v;
    }

    public Serializable getSerializable(E key) {
        Object o = getObject(key);
        if (o == null) {
            return null;
        }
        if (o instanceof Serializable) {
            return (Serializable) o;
        } else {
            return null;
        }
    }

    public final Object getObject(E key) {
        return mMap.get(key);
    }

    public <T extends List> T getList(E key) {
        Object o = getObject(key);
        if (o instanceof List) {
            return (T) mMap.get(key);
        } else {
            return null;
        }
    }

    public <T extends EVal> T getEv(E key) {
        Object o = getObject(key);
        if (o instanceof EVal) {
            return (T) o;
        } else {
            return null;
        }
    }

    /**
     * 获取所有已赋值的枚举的合集, 如有字段未赋值, 不在此集合里
     *
     * @return key name 集合
     * @see EVal#getEnumFields()
     */
    public List<E> getValidEnumFields() {
        List<E> keyNames = new ArrayList<>();
        keyNames.addAll(mMap.keySet());
        return keyNames;
    }

    /**
     * 获取枚举里所有属性的集合
     *
     * @return
     */
    public List<E> getEnumFields() {
        Class<E> entityClass = GenericUtil.getClassType(getClass());
        return Arrays.asList(entityClass.getEnumConstants());
    }

    @Override
    protected void finalize() throws Throwable {
        clear();
    }

    @Override
    public EVal clone() {
        EVal val = create(getClass());

        Observable.fromIterable(getEnumFields())
                .subscribe(e -> {
                    Object o = mMap.get(e);
                    if (o == null) {
                        return;
                    }

                    if (o instanceof EVal) {
                        EVal ev = (EVal) o;
                        val.put(e, ev.clone());
                    } else if (o instanceof List) {
                        List list = (List) o;
                        val.put(e, cloneArray(list));
                    } else {
                        // 基础类型
                        val.put(e, new String(getString(e)));
                    }

                });

        return val;
    }

    private List<?> cloneArray(List<?> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        List<Object> newList = new ArrayList<>();

        Observable.fromIterable(list)
                .subscribe(o -> {
                    if (o instanceof EVal) {
                        EVal ev = (EVal) o;
                        newList.add(ev.clone());
                    } else if (o instanceof List) {
                        newList.add(cloneArray(list));
                    } else {
                        // 基础类型
                        newList.add(o);
                    }
                });

        return newList;
    }

    /**
     * 创建实例
     *
     * @param clz
     * @param text {@link JSONObject}类型的text
     * @return
     */
    public static <T extends EVal> T create(Class<T> clz, String text) {
        T t = create(clz);
        if (t == null) {
            return null;
        }

        if (TextUtil.isNotEmpty(text)) {
            t.parse(text);
        }

        return t;
    }

    /**
     * 创建实例
     *
     * @param clz
     * @return
     */
    public static <T extends EVal> T create(Class<T> clz) {
        return ReflectionUtil.newDeclaredInst(clz);
    }

    /***********************************
     * 解析相关
     ****************************/

    /**
     * 转成通用的json格式
     *
     * @return
     */
    public String toJson() {
        return toJsonObject().toString();
    }

    private JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            Class clz = GenericUtil.getClassType(getClass());

            Field[] fields = clz.getFields();
            for (int i = 0; i < fields.length; ++i) {
                Field f = fields[i];
                // 必须通过valueOf获取E, 因为fields[]的排列不是有序的1
                E e = (E) E.valueOf(clz, f.getName());
                if (getObject(e) == null) {
                    continue;
                }

                if (f.isAnnotationPresent(Bind.class)) {
                    Bind annotation = f.getAnnotation(Bind.class);

                    Class val = annotation.value();
                    if (!val.equals(EVal.class)) {
                        obj.put(e.name(), getEv(e).toJsonObject());
                        continue;
                    }

                    Class asList = annotation.asList();
                    if (!asList.equals(void.class)) {
                        JSONArray array = new JSONArray();
                        if (isEValType(asList)) {
                            List<EVal> list = getList(e);
                            for (EVal item : list) {
                                array.put(item.toJsonObject());
                            }
                        } else {
                            // 基础数据类型
                            List<Object> list = getList(e);
                            for (Object o : list) {
                                array.put(o);
                            }
                        }

                        obj.put(e.name(), array);
                        continue;
                    }


                } else {
                    obj.put(e.name(), getString(e));
                }
            }
        } catch (Exception e) {
            YSLog.e(TAG, "toJson", e);
        }

        return obj;
    }

    public void parse(JSONObject obj) {
        if (obj == null) {
            return;
        }

        Class clz = GenericUtil.getClassType(getClass());
        Field[] fields = clz.getFields();

        for (int i = 0; i < fields.length; ++i) {
            Field f = fields[i];
            // 必须通过valueOf获取E, 因为fields[]的排列不是有序的
            E e = (E) E.valueOf(clz, f.getName());

            Object o = JsonUtil.getObject(obj, e.name());
            if (o == null) {
                continue;
            }

            if (f.isAnnotationPresent(Bind.class)) {
                Bind annotation = f.getAnnotation(Bind.class);
                Class val = annotation.value();
                if (!val.equals(EVal.class)) {
                    put(e, JsonUtil.getEV(annotation.value(), obj.optJSONObject(e.name())));
                    continue;
                }

                Class asList = annotation.asList();
                if (isEValType(asList)) {
                    put(e, JsonUtil.getEVs(asList, obj.optJSONArray(e.name())));
                } else {
                    // 基础数据类型
                    List list = new ArrayList<>();
                    JSONArray jsonArray = obj.optJSONArray(e.name());
                    for (int j = 0; j < jsonArray.length(); j++) {
                        if (String.class.isAssignableFrom(asList)) {
                            list.add(jsonArray.optString(j));
                        } else if (Integer.class.isAssignableFrom(asList)) {
                            list.add(jsonArray.optInt(j));
                        } else if (Float.class.isAssignableFrom(asList)
                                || Double.class.isAssignableFrom(asList)) {
                            list.add(jsonArray.optDouble(j));
                        } else {
                            list.add(jsonArray.opt(j));
                        }
                    }
                    put(e, list);
                }
            } else {
                // 没有注释使用默认解析方式
                put(e, o);
            }
        }
    }

    public void parse(String text) {
        try {
            JSONObject obj = new JSONObject(text);
            parse(obj);
        } catch (JSONException e) {
            YSLog.e(TAG, "parse", e);
        }
    }

    private boolean isEValType(Class clz) {
        return EVal.class.isAssignableFrom(clz);
    }
}
