package lib.ys.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lib.network.model.interfaces.IListResult;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.model.EVal;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JsonUtil {

    private static final String TAG = JsonUtil.class.getSimpleName();

    public static Object getObject(JSONObject json, String tag) {
        Object ret = json.opt(tag);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public static String getString(JSONObject json, String tag) {
        String ret = null;
        try {
            ret = json.optString(tag);
        } catch (Exception e) {
            YSLog.d(TAG, tag, e);
        }

        return ret;
    }

    public static int getInt(JSONObject json, String tag) {
        int ret = 0;
        try {
            ret = json.optInt(tag);
        } catch (Exception e) {
            YSLog.d(TAG, tag, e);
        }
        return ret;
    }

    public static long getLong(JSONObject json, String tag) {
        long ret = 0;
        try {
            ret = json.optLong(tag);
        } catch (Exception e) {
            YSLog.d(TAG, tag, e);
        }
        return ret;
    }

    public static double getDouble(JSONObject json, String tag) {
        double ret = 0;
        try {
            ret = json.optDouble(tag);
        } catch (Exception e) {
            YSLog.d(TAG, tag, e);
        }
        return ret;
    }

    /**
     * 通用解析列表数据方式
     *
     * @param clz   一定要是继承EnumsValue的对象
     * @param r
     * @param array
     * @throws JSONException
     */
    public static <T extends EVal> void setEVs(Class<T> clz, IListResult<T> r, JSONArray array) throws JSONException {
        if (array == null) {
            return;
        }
        r.setData(getEVs(clz, array));
    }

    /**
     * 通用解析单一数据方式
     *
     * @param clz
     * @param r
     * @param object
     * @throws JSONException
     */
    public static <T extends EVal> void setEV(Class<T> clz, IResult<T> r, JSONObject object) throws JSONException {
        if (object == null) {
            return;
        }
        r.setData(getEV(clz, object));
    }

    /**
     * 解析单个ev
     *
     * @param clz
     * @param object
     * @return
     */
    public static <T extends EVal> T getEV(Class<T> clz, JSONObject object) {
        if (object == null) {
            return null;
        }

        T t = ReflectionUtil.newInst(clz);
        if (t == null) {
            return null;
        }

        t.parse(object);

        return t;
    }

    /**
     * 解析item 数组
     *
     * @param clz
     * @param array
     * @return
     */
    public static <T extends EVal> List<T> getEVs(Class<T> clz, JSONArray array) {
        if (array == null) {
            return null;
        }

        List<T> ts = new ArrayList<>();
        for (int i = 0; i < array.length(); ++i) {
            JSONObject object = array.optJSONObject(i);
            if (object == null) {
                continue;
            }

            ts.add(getEV(clz, object));
        }
        return ts;
    }

    /**
     * json的字符串转换成map
     *
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(String json) {
        Map<String, Object> map = new HashMap<>();
        try {
            JSONObject object = new JSONObject(json);
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, object.get(key));
            }
        } catch (JSONException e) {
            YSLog.e(TAG, "toMap", e);
        }

        return map;
    }
}
