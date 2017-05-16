package lib.ys.network.result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lib.ys.model.EVal;
import lib.ys.util.JsonUtil;
import lib.ys.util.TextUtil;

@SuppressWarnings("rawtypes")
public class JsonParserEx {

    protected static <T extends EVal> void setEV(Class<T> clz, IResponse<T> r, JSONObject obj) throws JSONException {
        JsonUtil.setEV(clz, r, obj);
    }

    protected static <T extends EVal> void setEVs(Class<T> clz, IListResponse<T> r, JSONArray array) throws JSONException {
        JsonUtil.setEVs(clz, r, array);
    }

    protected static <T extends EVal> T getEV(Class<T> clz, JSONObject obj) {
        return JsonUtil.getEV(clz, obj);
    }

    protected static <T extends EVal> T getEV(Class<T> clz, String text) throws JSONException {
        if (TextUtil.isEmpty(text)) {
            return null;
        }

        return JsonUtil.getEV(clz, new JSONObject(text));
    }

    protected static <T extends EVal> List<T> getEVs(Class<T> clz, JSONArray array) {
        return JsonUtil.getEVs(clz, array);
    }

    protected static Object getObject(JSONObject obj, Enum<?> tag) {
        return getObject(obj, tag.name());
    }

    protected static Object getObject(JSONObject obj, String tag) {
        if (obj == null) {
            return null;
        }
        return JsonUtil.getObject(obj, tag);
    }

    protected static String getString(JSONObject obj, String tag) {
        if (obj == null) {
            return null;
        }
        return JsonUtil.getString(obj, tag);
    }

    protected static String getString(JSONObject obj, Enum<?> tag) {
        return getString(obj, tag.name());
    }

    protected static JSONObject getJSONObject(JSONObject obj, String tag) throws JSONException {
        if (obj == null) {
            return null;
        }
        return obj.optJSONObject(tag);
    }

    protected static JSONObject getJSONObject(JSONObject obj, Enum e) throws JSONException {
        if (obj == null) {
            return null;
        }
        return obj.optJSONObject(e.name());
    }

    protected static JSONArray getJSONArray(JSONObject obj, String tag) throws JSONException {
        if (obj == null) {
            return null;
        }
        return obj.optJSONArray(tag);
    }

    protected static JSONArray getJSONArray(JSONObject obj, Enum e) throws JSONException {
        if (obj == null) {
            return null;
        }
        return obj.optJSONArray(e.name());
    }

    protected static int getInt(JSONObject obj, Enum<?> tag) {
        return getInt(obj, tag.name());
    }

    protected static int getInt(JSONObject obj, String tag) {
        return JsonUtil.getInt(obj, tag);
    }

    protected static long getLong(JSONObject obj, String tag) {
        return JsonUtil.getLong(obj, tag);
    }

    protected static long getLong(JSONObject obj, Enum<?> tag) {
        return getLong(obj, tag.name());
    }
}
