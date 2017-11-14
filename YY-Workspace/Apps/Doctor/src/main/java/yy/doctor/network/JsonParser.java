package yy.doctor.network;

import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import lib.ys.model.EVal;
import lib.ys.model.MapList;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectUtil;
import lib.yy.network.BaseJsonParser;
import lib.yy.network.Result;
import yy.doctor.model.BaseGroup;

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
     * @param <C>      child类型
     * @param <G>      group类型
     * @return
     * @throws JSONException
     */
    public static <E extends Enum<E>, C extends EVal<E>, G extends BaseGroup<C>> Result<G> groupIndex(String text, Class<G> groupClz, E key) throws JSONException {
        Result<G> retResult = new Result<>();

        Class<C> childClz = GenericUtil.getClassType(groupClz);

        Result<C> r = evs(text, childClz);
        retResult.setCode(r.getCode());
        retResult.setMessage(r.getMessage());

        if (!r.isSucceed()) {
            return retResult;
        }

        MapList<String, G> mapList = new MapList<>();

        List<C> list = r.getList();
        if (list.isEmpty()) {
            retResult.setData(mapList);
            return retResult;
        }

        for (C c : list) {
            String tag = c.getString(key);
            G g = mapList.getByKey(tag);
            if (g == null) {
                g = ReflectUtil.newInst(groupClz);
                g.setTag(tag);
                mapList.add(tag, g);
            }

            g.addChild(c);
        }

        Collections.sort(mapList, (lhs, rhs) -> lhs.getTag().charAt(0) - rhs.getTag().charAt(0));
        retResult.setData(mapList);
        return retResult;
    }

}
