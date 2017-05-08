package yy.doctor.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lib.ys.model.EVal;
import lib.yy.network.BaseJsonParser;
import lib.yy.network.ListResponse;
import yy.doctor.activity.EValParser;
import yy.doctor.model.home.Home;
import yy.doctor.model.home.HomeMeeting;
import yy.doctor.model.unitnum.GroupUnitNum;
import yy.doctor.model.unitnum.UnitNum;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class JsonParser extends BaseJsonParser implements EValParser {

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

    public static ListResponse<GroupUnitNum> unitNums(String text) throws JSONException {

        ListResponse<GroupUnitNum> r = new ListResponse<>();
        List<GroupUnitNum> gs = new ArrayList<>();

        List<UnitNum> nums = evs(text, UnitNum.class).getData();


        r.setData(gs);
        return r;
    }

    @Override
    public <T> T parse(String text, Class<T> c) throws JSONException {
        JSONObject object = new JSONObject(text);

        int code;
        if (object.has(GlobalTag.err_code)) {
            code = getInt(object, GlobalTag.err_code);
        } else {
            code = ErrorCode.un_know;
        }

        if (code != ErrorCode.ok) {
            return null;
        }

        Class<? extends EVal> ec = (Class<? extends EVal>) c;

        JSONObject obj = new JSONObject(text);
        JSONObject o = obj.optJSONObject(GlobalTag.data);
        if (o != null) {
            return (T) getEV(ec, o);
        } else {
            JSONArray array = obj.optJSONArray(GlobalTag.data);
            if (array != null) {
                return (T) getEVs(ec, array);
            } else {
                return null;
            }
        }
    }
}
