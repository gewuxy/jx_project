package yy.doctor.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        ListResponse<GroupUnitNum> r = new ListResponse<>();
        List<GroupUnitNum> gs = new ArrayList<>();

        List<UnitNum> nums = evs(text, UnitNum.class).getData();

        String letter = null;

        String lastLetter = null;
        GroupUnitNum lastGroupUnitNum = null;

        for (int i = 0; i < nums.size(); i++) {

            letter = nums.get(i).getString(TUnitNum.alpha);

            if (i == 0) {

                GroupUnitNum groupUnitNum = new GroupUnitNum();
                groupUnitNum.setLetter(letter);
                groupUnitNum.add(nums.get(i));

                lastLetter = letter;
                lastGroupUnitNum = groupUnitNum;

            } else {

                if (letter.equals(lastLetter)) {

                    lastGroupUnitNum.add(nums.get(i));

                } else {

                    gs.add(lastGroupUnitNum);

                    GroupUnitNum groupUnitNum = new GroupUnitNum();
                    groupUnitNum.setLetter(letter);
                    groupUnitNum.add(nums.get(i));

                    lastLetter = letter;
                    lastGroupUnitNum = groupUnitNum;

                    if (i == nums.size() - 1) {
                        gs.add(groupUnitNum);
                    }

                }

            }

        }

        r.setData(gs);
        return r;
    }

}
