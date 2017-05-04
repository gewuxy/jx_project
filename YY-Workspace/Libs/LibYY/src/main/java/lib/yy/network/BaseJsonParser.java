package lib.yy.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.ys.model.EVal;
import lib.ys.network.resp.IResponse;
import lib.ys.network.resp.JsonParserEx;

/**
 * @author yuansui
 */
public class BaseJsonParser extends JsonParserEx {

    public @interface GlobalTag {
        String err_code = "code";
        String msg = "err"; // 只有code != 0的时候才有
        String data = "data";
    }

    public @interface ErrorCode {
        int ok = 0;
        int un_know = -1000;
    }

    /**
     * 只解析是否错误及错误的信息
     *
     * @param text
     * @param r
     * @return
     * @throws JSONException
     */
    private static boolean error(String text, IResponse r) throws JSONException {
        JSONObject object = new JSONObject(text);

        int code;
        if (object.has(GlobalTag.err_code)) {
            code = getInt(object, GlobalTag.err_code);
        } else {
            code = ErrorCode.un_know;
        }

        r.setCode(code);
        r.setError(getString(object, GlobalTag.msg));

        return code != ErrorCode.ok;
    }

    /**
     * 获取Json object, 同时判断成功失败
     *
     * @param text
     * @param r
     * @return
     * @throws JSONException
     */
    protected static JSONObject getGlobalJsonObject(String text, IResponse r) throws JSONException {
        if (error(text, r)) {
            return null;
        }
        JSONObject obj = new JSONObject(text);
        return obj.optJSONObject(GlobalTag.data);
    }

    /**
     * 获取Json array, 同时判断成功失败
     *
     * @param text
     * @param r
     * @return
     * @throws JSONException
     */
    protected static JSONArray getGlobalJsonArray(String text, IResponse r) throws JSONException {
        if (error(text, r)) {
            return null;
        }
        JSONObject obj = new JSONObject(text);
        return obj.optJSONArray(GlobalTag.data);
    }

    /**
     * 直接解析成EVal的对象
     *
     * @param text
     * @param clz
     * @param <T>
     * @return
     * @throws JSONException
     */
    public static <T extends EVal> Response<T> ev(String text, Class<T> clz) throws JSONException {
        Response<T> r = new Response<>();
        setEV(clz, r, getGlobalJsonObject(text, r));
        return r;
    }

    /**
     * 直接解析成EVal的list, 来源于data->list的标签底下
     *
     * @param text
     * @param clz
     * @param <T>
     * @return
     * @throws JSONException
     */
    public static <T extends EVal> ListResponse<T> evs(String text, Class<T> clz) throws JSONException {
        ListResponse<T> r = new ListResponse<>();
        setEVs(clz, r, getGlobalJsonArray(text, r));
        return r;
    }
}
