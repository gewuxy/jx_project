package jx.csp.network;

import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import lib.ys.model.EVal;
import lib.ys.model.MapList;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectUtil;
import lib.yy.network.BaseJsonParser;
import lib.yy.network.ListResult;
import jx.csp.model.BaseGroup;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class JsonParser extends BaseJsonParser {

    /**
     * 解析带有index的group泛型数据
     *
     * @param text
     * @param groupClz
     * @param key
     * @param <E>      child里的enum key类型
     * @param <CHILD>  child类型
     * @param <GROUP>  group类型
     * @return
     * @throws JSONException
     */
    public static <E extends Enum<E>, CHILD extends EVal<E>, GROUP extends BaseGroup<CHILD>> ListResult<GROUP> groupIndex(String text, Class<GROUP> groupClz, E key) throws JSONException {
        ListResult<GROUP> retResult = new ListResult<>();

        Class<CHILD> childClz = GenericUtil.getClassType(groupClz);

        ListResult<CHILD> r = evs(text, childClz);
        retResult.setCode(r.getCode());
        retResult.setMessage(r.getMessage());

        if (!r.isSucceed()) {
            return retResult;
        }

        MapList<String, GROUP> mapList = new MapList<>();

        List<CHILD> list = r.getData();
        if (list.isEmpty()) {
            retResult.setData(mapList);
            return retResult;
        }

        for (CHILD child : list) {
            String tag = child.getString(key);
            GROUP g = mapList.getByKey(tag);
            if (g == null) {
                g = ReflectUtil.newInst(groupClz);
                g.setTag(tag);
                mapList.add(tag, g);
            }

            g.addChild(child);
        }

        Collections.sort(mapList, (lhs, rhs) -> lhs.getTag().charAt(0) - rhs.getTag().charAt(0));
        retResult.setData(mapList);
        return retResult;
    }

}
