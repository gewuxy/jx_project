package lib.ys.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java8.lang.Iterables;
import lib.ys.ConstantsEx;
import lib.ys.LogMgr;
import lib.ys.model.inject.BindInit;
import lib.ys.model.inject.BindList;
import lib.ys.model.inject.BindObj;
import lib.ys.util.GenericUtil;
import lib.ys.util.JsonUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.TextUtil;

/**
 * 使用枚举的hash map, Key是任意枚举
 *
 * @param <E> 任意枚举
 * @author yuansui
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
abstract public class EVal<E extends Enum<E>> implements Serializable, Cloneable {

    protected String TAG = getClass().getSimpleName();

    private static final String KKeyData = "data";
    private static final String KKeyClass = "class";

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
            if (f.isAnnotationPresent(BindInit.class)) {
                // 有初始化字段
                E e = (E) E.valueOf(clz, f.getName());
                BindInit annotation = f.getAnnotation(BindInit.class);
                int asInt = annotation.asInt();
                if (asInt != ConstantsEx.KInvalidValue) {
                    put(e, asInt);
                    continue;
                }

                long asLong = annotation.asLong();
                if (asLong != ConstantsEx.KInvalidValue) {
                    put(e, asLong);
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

    public final <T extends EVal<E>> T put(EVal<E> o) {
        Iterables.forEach(getEnumFields(), e -> put(e, o.getObject(e)));
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
        EVal val = newInst(getClass());

        Iterables.forEach(getEnumFields(), e -> {
            Object o = mMap.get(e);
            if (o == null) {
                return;
            }

            if (o instanceof EVal) {
                EVal ev = (EVal) o;
                val.mMap.put(e, ev.clone());
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
        Iterables.forEach(list, o -> {
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
     * 根据外部数据更新
     *
     * @param source
     */
    public <T extends EVal<E>> void update(T source) {
        if (source == null) {
            return;
        }

        Iterables.forEach(getEnumFields(), e -> put(e, source.getObject(e)));
    }

    /**
     * 转成通用的json格式
     *
     * @return
     */
    public String toCommonJson() {
        JSONObject obj = toJsonObject();
        return obj.toString();
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

                if (f.isAnnotationPresent(BindList.class)) {
                    JSONArray array = new JSONArray();
                    List<EVal> list = getList(e);
                    for (int j = 0; j < list.size(); ++j) {
                        EVal eVal = list.get(j);
                        array.put(eVal.toJsonObject());
                    }
                    obj.put(e.name(), array);
                } else if (f.isAnnotationPresent(BindObj.class)) {
                    obj.put(e.name(), getEv(e).toJsonObject());
                } else {
                    obj.put(e.name(), getString(e));
                }
            }
        } catch (Exception e) {
            LogMgr.e(TAG, "toCommonJson", e);
        }

        return obj;
    }

    /**
     * 转换成本地存储专用的json格式
     *
     * @return
     */
    public String toStoreJson() {
        return toStoreJsonObj(this).toString();
    }

    /**
     * 拼接JsonObject
     *
     * @param t
     * @return
     */
    private <T extends EVal<E>> JSONObject toStoreJsonObj(T t) {
        JSONObject jsonObject = new JSONObject();
        Iterables.forEach(getEnumFields(), e -> {
            try {
                Object obj = t.getObject(e);
                if (obj == null) {
                    return;
                }

                JSONObject subJson = new JSONObject();
                subJson.put(KKeyClass, obj.getClass().getName());

                if (obj instanceof EVal) {
                    subJson.put(KKeyData, toStoreJsonObj((EVal) obj));
                } else if (obj instanceof ArrayList) {
                    subJson.put(KKeyData, toJsonArray((ArrayList) obj));
                } else {
                    subJson.put(KKeyData, t.getString(e));
                }

                jsonObject.put(e.name(), subJson);
            } catch (Exception ex) {
                LogMgr.e(TAG, ex);
            }
        });

        return jsonObject;
    }

    private JSONArray toJsonArray(List list) throws JSONException {
        JSONArray array = new JSONArray();

        for (int j = 0; j < list.size(); ++j) {
            Object listObject = list.get(j);
            if (listObject instanceof EVal) {
                EVal value = (EVal) listObject;

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(KKeyClass, value.getClass().getName());
                jsonObject.put(KKeyData, toStoreJsonObj(value));

                array.put(jsonObject);
            } else if (listObject instanceof List) {
                toJsonArray((List) listObject);
            } else {
                array.put(listObject);
            }
        }

        return array;
    }

    /**
     * 根据json设置属性
     *
     * @param text
     */
    public void set(String text) {
        try {
            setStoreText(this, text);
        } catch (JSONException e) {
            LogMgr.e(TAG, "put", e);
        }
    }

    private <T extends EVal<E>> void setStoreText(T t, String text) throws JSONException {
        if (t == null) {
            return;
        }

        JSONObject jsonObj = new JSONObject(text);

        for (E e : t.getEnumFields()) {
            String content = JsonUtil.getString(jsonObj, e.name());
            if (TextUtils.isEmpty(content)) {
                // 无数据
                continue;
            }

            JSONObject obj = new JSONObject(content);

            String className = JsonUtil.getString(obj, KKeyClass);

            Class clz;
            try {
                clz = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                LogMgr.e(TAG, className, ex);
                continue;
            }

            String data = JsonUtil.getString(obj, KKeyData);

            if (Integer.class.isAssignableFrom(clz)
                    || Boolean.class.isAssignableFrom(clz)
                    || String.class.isAssignableFrom(clz)
                    || Long.class.isAssignableFrom(clz)
                    || Double.class.isAssignableFrom(clz)) {
                // 基础类型, 直接赋值
                t.put(e, data);
            } else if (List.class.isAssignableFrom(clz)) {
                // 数组特殊处理
                JSONArray array = new JSONArray(data);
                t.put(e, getList(array));
            } else if (isEValType(clz)) {
                t.put(e, newStoreInst(clz, data));
            }

        }
    }

    /**
     * 获取数组, 里面可以是任意类型的数据
     *
     * @param array
     * @return
     * @throws JSONException
     */
    private List<Object> getList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); ++i) {
            JSONObject object = array.getJSONObject(i);

            String className = JsonUtil.getString(object, KKeyClass);
            Class clz;
            try {
                clz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                LogMgr.e(TAG, className, e);
                continue;
            }

            String data = JsonUtil.getString(object, KKeyData);

            if (Integer.class.isAssignableFrom(clz)
                    || Boolean.class.isAssignableFrom(clz)
                    || String.class.isAssignableFrom(clz)
                    || Long.class.isAssignableFrom(clz)
                    || Double.class.isAssignableFrom(clz)) {
                // 基础类型, 直接赋值
                list.add(data);
            } else if (List.class.isAssignableFrom(clz)) {
                JSONArray arrayNew = new JSONArray(data);
                list.add(getList(arrayNew));
            } else if (isEValType(clz)) {
                list.add(newStoreInst(clz, data));
            }
        }

        return list;
    }

    /**
     * 获取EVal的实例
     *
     * @param clz
     * @return
     */
    public static <T extends EVal> T newInst(Class<T> clz) {
        return ReflectionUtil.newDeclaredInst(clz);
    }

    /**
     * 获取EVal的实例
     *
     * @param clz
     * @param text {@link JSONObject}类型的text
     * @return
     */
    public static <T extends EVal> T newJSONInst(Class<T> clz, String text) {
        T t = newInst(clz);
        if (t == null) {
            return null;
        }

        if (TextUtil.isNotEmpty(text)) {
            try {
                t.parse(text);
            } catch (JSONException e) {
                LogMgr.e("EVal", "newJSONInst", e);
            }
        }

        return t;
    }

    /**
     * 获取EVal的实例
     *
     * @param clz
     * @param text 经过{{@link #toStoreJson()}}转换的text
     * @param <T>
     * @return
     */
    public static <T extends EVal> T newStoreInst(Class<T> clz, String text) {
        T t = newInst(clz);
        if (t == null) {
            return t;
        }

        if (TextUtil.isNotEmpty(text)) {
            t.set(text);
        }

        return t;
    }

    /***********************************
     * 解析相关
     */
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

            if (f.isAnnotationPresent(BindList.class)) {
                BindList annotation = f.getAnnotation(BindList.class);
                Class val = annotation.value();
                if (isEValType(val)) {
                    put(e, JsonUtil.getEVs(val, obj.optJSONArray(e.name())));
                } else {
                    // 先不考虑
                }
                continue;
            } else if (f.isAnnotationPresent(BindObj.class)) {
                BindObj annotation = f.getAnnotation(BindObj.class);
                put(e, JsonUtil.getEV(annotation.value(), obj.optJSONObject(e.name())));
            } else {
                // 没有注释使用默认解析方式
                put(e, JsonUtil.getObject(obj, e.name()));
            }
        }
    }

    private boolean isEValType(Class clz) {
        return EVal.class.isAssignableFrom(clz);
    }

    public void parse(String text) throws JSONException {
        JSONObject obj = new JSONObject(text);
        parse(obj);
    }
}
