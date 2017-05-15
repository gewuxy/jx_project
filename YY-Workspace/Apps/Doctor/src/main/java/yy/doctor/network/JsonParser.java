package yy.doctor.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lib.ys.model.EVal;
import lib.ys.model.MapList;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;
import lib.yy.network.BaseJsonParser;
import lib.yy.network.ListResp;
import yy.doctor.model.BaseGroup;
import yy.doctor.model.home.Home;
import yy.doctor.model.home.HomeMeeting;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class JsonParser extends BaseJsonParser {

    public static ListResp<Home> home(String text) throws JSONException {
        ListResp<Home> r = new ListResp<>();

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

    /**
     * 解析带有index的group泛型数据
     *
     * @param text
     * @param groupClz
     * @param key
     * @param <E>      child里的enum key类型
     * @param <C>      child类型
     * @param <T>      group类型
     * @return
     * @throws JSONException
     */
    public static <E extends Enum<E>, C extends EVal<E>, T extends BaseGroup<C>> ListResp<T> groupIndex(String text, Class<T> groupClz, E key) throws JSONException {
        ListResp<T> retResp = new ListResp<>();

        Class<C> childClz = GenericUtil.getClassType(groupClz);

        ListResp<C> r = evs(text, childClz);
        retResp.setCode(r.getCode());
        retResp.setError(r.getError());

        if (!r.isSucceed()) {
            return retResp;
        }

        List<C> list = r.getData();
        if (list.isEmpty()) {
            return retResp;
        }

        MapList<String, T> mapList = new MapList<>();
        for (C child : list) {
            String tag = child.getString(key);
            T g = mapList.getByKey(tag);
            if (g == null) {
                g = ReflectionUtil.newInst(groupClz);
                g.setTag(tag);
                mapList.add(tag, g);
            }

            g.add(child);
        }

        retResp.setData(mapList);
        return retResp;
    }

}
