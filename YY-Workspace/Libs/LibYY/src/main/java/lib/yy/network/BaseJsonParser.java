package lib.yy.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.ys.model.EVal;
import lib.ys.network.resp.IResp;
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
    protected static boolean error(String text, IResp r) throws JSONException {
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
     * 简易判断网络结果
     *
     * @param text
     * @return
     * @throws JSONException
     */
    public static Resp<String> error(String text) throws JSONException {
        Resp<String> r = new Resp<>();
        error(text, r);
        return r;
    }

    /**
     * 获取Json object, 同时判断成功失败
     *
     * @param text
     * @param r
     * @return
     * @throws JSONException
     */
    protected static JSONObject getGlobalJsonObject(String text, IResp r) throws JSONException {
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
    protected static JSONArray getGlobalJsonArray(String text, IResp r) throws JSONException {
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
    public static <T extends EVal> Resp<T> ev(String text, Class<T> clz) throws JSONException {
        Resp<T> r = new Resp<>();
        setEV(clz, r, getGlobalJsonObject(text, r));
        return r;
    }

    /**
     * 直接解析成EVal的list
     *
     * @param text
     * @param clz
     * @param <T>
     * @return
     * @throws JSONException
     */
    public static <T extends EVal> ListResp<T> evs(String text, Class<T> clz) throws JSONException {
        ListResp<T> r = new ListResp<>();
        setEVs(clz, r, getGlobalJsonArray(text, r));
        return r;
    }
}
