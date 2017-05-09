package yy.doctor.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lib.ys.model.MapList;
import lib.yy.network.BaseJsonParser;
import lib.yy.network.ListResponse;
import yy.doctor.model.home.Home;
import yy.doctor.model.home.HomeMeeting;
import yy.doctor.model.unitnum.GroupUnitNum;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class JsonParser extends BaseJsonParser {

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
        ListResponse<GroupUnitNum> retResp = new ListResponse<>();

        ListResponse<UnitNum> r = evs(text, UnitNum.class);
        retResp.setCode(r.getCode());
        retResp.setError(r.getError());

        if (!r.isSucceed()) {
            return retResp;
        }

        List<UnitNum> nums = r.getData();
        if (nums.isEmpty()) {
            return retResp;
        }

        MapList<String, GroupUnitNum> mapList = new MapList<>();
        for (UnitNum num : nums) {
            String letter = num.getString(TUnitNum.alpha);
            GroupUnitNum g = mapList.getByKey(letter);
            if (g == null) {
                g = new GroupUnitNum();
                g.setLetter(letter);
                mapList.add(letter, g);
            }

            g.add(num);
        }

        retResp.setData(mapList);
        return retResp;
    }

}
