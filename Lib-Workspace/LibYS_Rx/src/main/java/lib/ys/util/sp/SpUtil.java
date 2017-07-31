package lib.ys.util.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lib.ys.AppEx;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.model.EVal;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SpUtil {

    private static final String TAG = SpUtil.class.getSimpleName();

    /**
     * 根据类型自动匹配存储
     *
     * @param sp
     * @param key
     * @param value
     * @return
     */
    public static boolean save(SharedPreferences sp, String key, Object value) {
        if (value instanceof Integer) {
            return save(sp, key, (Integer) value);
        } else if (value instanceof String) {
            return save(sp, key, (String) value);
        } else if (value instanceof Long) {
            return save(sp, key, (Long) value);
        } else if (value instanceof Boolean) {
            return save(sp, key, (Boolean) value);
        } else if (value instanceof EVal) {
            return saveEVal(sp, key, (EVal) value);
        } else if (value instanceof Serializable) {
            return save(sp, key, (Serializable) value);
        } else {
            YSLog.d(TAG, "未知类型 = " + value);
            return false;
        }
    }

    public static boolean save(String spName, String key, Object value) {
        SharedPreferences sp = AppEx.ct().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return save(sp, key, value);
    }

    private static boolean save(SharedPreferences sp, String key, Integer value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    private static boolean save(SharedPreferences sp, String key, Long value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    private static boolean save(SharedPreferences sp, String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private static boolean save(SharedPreferences sp, String key, Boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private static boolean save(SharedPreferences sp, String key, Serializable data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            String dataString = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            save(sp, key, dataString);
            oos.close();
            baos.close();
        } catch (IOException e) {
            YSLog.e(TAG, e);
            return false;
        }
        return true;
    }

    /**
     * 保存某个 EnumsValue子类的数据, 可以兼容新老版本的数据不一样
     * PS: 如果直接使用保存序列化的模式, 修改新数据会导致数据不一样的崩溃
     *
     * @param sp
     * @param key
     * @param val
     * @return
     */
    private static boolean saveEVal(SharedPreferences sp, String key, EVal val) {
        String content = val.toJson();
        if (!TextUtils.isEmpty(key)) {
            return save(sp, key, content);
        } else {
            return save(sp, val.getClass().getSimpleName(), content);
        }
    }

    public static boolean contains(SharedPreferences sp, String key) {
        return sp.contains(key);
    }

    public static Integer getInt(SharedPreferences sp, String key) {
        return sp.getInt(key, ConstantsEx.KErrNotFound);
    }

    public static Integer getInt(SharedPreferences sp, String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public static Integer getInt(String spName, String key, int defValue) {
        SharedPreferences sp = AppEx.ct().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    /**
     * Get Long Value
     */
    public static long getLong(SharedPreferences sp, String key) {
        return sp.getLong(key, ConstantsEx.KErrNotFound);
    }

    public static long getLong(SharedPreferences sp, String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    /**
     * Get String Value
     */
    public static String getString(SharedPreferences sp, String key) {
        return sp.getString(key, ConstantsEx.KEmptyValue);
    }

    public static String getString(SharedPreferences sp, String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public static String getString(String spName, String key, String defValue) {
        SharedPreferences sp = AppEx.ct().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return getString(sp, key, defValue);
    }

    /**
     * get boolean value
     */
    public static Boolean getBoolean(SharedPreferences sp, String key) {
        return sp.getBoolean(key, false);
    }

    public static Boolean getBoolean(SharedPreferences sp, String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public static Boolean getBoolean(String spName, String key, boolean defValue) {
        SharedPreferences sp = AppEx.ct().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return getBoolean(sp, key, defValue);
    }

    /**
     * clear all values
     */
    public static boolean clear(SharedPreferences sp) {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean remove(SharedPreferences sp, String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static boolean removeKeys(SharedPreferences sp, String... keys) {
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < keys.length; ++i) {
            editor.remove(keys[i]);
        }
        return editor.commit();
    }

    /**
     * @param sp
     * @param key
     * @return 没有值的时候返回null
     */
    public static Serializable getSerializable(SharedPreferences sp, String key) {
        String objBase64 = getString(sp, key);
        if (TextUtils.isEmpty(objBase64)) {
            return null;
        }
        byte[] base64Bytes = Base64.decode(objBase64.getBytes(), Base64.DEFAULT);

        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Serializable ret = (Serializable) ois.readObject();
            ois.close();
            bais.close();
            return ret;
        } catch (Exception e) {
            YSLog.e(TAG, e);
        }
        return null;
    }

    /***********************************************
     * EnumsValue---start
     */

    public static <T extends EVal> boolean isEVExist(SharedPreferences sp, String key, Class<T> clz) {
        if (TextUtils.isEmpty(key)) {
            // 使用类名作为key
            key = clz.getSimpleName();
        }

        String text = getString(sp, key);
        if (TextUtils.isEmpty(text)) {
            // 没有保存
            return false;
        }

        return EVal.create(clz) != null;
    }

    /**
     * 读取保存的EnumsValue数据
     *
     * @param sp
     * @param key 保存的关键字
     * @param clz
     * @return
     */
    public static <T extends EVal> T getEV(SharedPreferences sp, String key, Class<T> clz) {
        if (TextUtils.isEmpty(key)) {
            // 使用类名作为key
            key = clz.getSimpleName();
        }

        String text = getString(sp, key);
        if (TextUtils.isEmpty(text)) {
            // 没有保存
            return null;
        }

        T t = EVal.create(clz, text);
        if (t == null) {
            return null;
        }

        return t;
    }
}
