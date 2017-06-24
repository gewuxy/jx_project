package lib.yy.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.network.model.interfaces.IResult;
import lib.ys.model.EVal;
import lib.ys.network.result.JsonParserEx;
import lib.ys.util.res.ResLoader;
import lib.yy.R;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;

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
        int user_unauthenticated = 100;  //用户未认证
        int un_know = -1000;
        int server_err = -999;
    }

    /**
     * 只解析是否错误及错误的信息
     *
     * @param text
     * @param r
     * @return
     * @throws JSONException
     */
    protected static boolean error(String text, IResult r) {
        int code;
        String errorStr;
        try {
            JSONObject object = new JSONObject(text);
            if (object.has(GlobalTag.err_code)) {
                code = getInt(object, GlobalTag.err_code);
            } else {
                code = ErrorCode.un_know;
            }
            errorStr = getString(object, GlobalTag.msg);

        } catch (JSONException e) {
            code = ErrorCode.server_err;
            errorStr = ResLoader.getString(R.string.server_error);
        }
        r.setCode(code);
        r.setError(errorStr);

        if (code == ErrorCode.user_unauthenticated) {
            Notifier.inst().notify(NotifyType.token_out_of_date);
        }

        return code != ErrorCode.ok;
    }

    /**
     * 简易判断网络结果
     *
     * @param text
     * @return
     * @throws JSONException
     */
    public static Result<String> error(String text) throws JSONException {
        Result<String> r = new Result<>();
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
    protected static JSONObject getGlobalJsonObject(String text, IResult r) throws JSONException {
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
    protected static JSONArray getGlobalJsonArray(String text, IResult r) throws JSONException {
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
    public static <T extends EVal> Result<T> ev(String text, Class<T> clz) throws JSONException {
        Result<T> r = new Result<>();
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
    public static <T extends EVal> ListResult<T> evs(String text, Class<T> clz) throws JSONException {
        ListResult<T> r = new ListResult<>();
        setEVs(clz, r, getGlobalJsonArray(text, r));
        return r;
    }
}
