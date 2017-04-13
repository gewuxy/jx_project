package yy.doctor.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.ys.model.EVal;
import lib.ys.network.resp.JsonParserEx;
import lib.yy.network.ListResponse;
import yy.doctor.model.home.Home;
import yy.doctor.model.home.HomeMeeting;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class JsonParser extends JsonParserEx {

    public static ListResponse<Home> home(String text) throws JSONException {
        ListResponse<Home> r = new ListResponse<>();

        JSONObject jsonObject = new JSONObject(text);
        JSONArray array = jsonObject.optJSONArray("");
        if (array == null) {
            return r;
        }

        for (int i = 0; i < array.length(); ++i) {
            JSONObject object = array.optJSONObject(i);
            Home h = getEV(HomeMeeting.class, object);
            r.add(h);
        }

        return r;
    }

    public <T extends EVal> T ev(Class<T> clz, String text) throws JSONException {
        return getEV(clz, text);
    }
}
