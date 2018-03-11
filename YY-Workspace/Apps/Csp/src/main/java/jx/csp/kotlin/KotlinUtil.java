package jx.csp.kotlin;

import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import jx.csp.App;
import jx.csp.kotlin.ui.GroupWallet;
import jx.csp.kotlin.ui.Wallet;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import lib.jx.network.Result;
import lib.network.model.NetworkReq;
import lib.ys.model.EVal;
import lib.ys.model.MapList;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectUtil;

/**
 * kotlin没办法调用的Util(框架不支持?kapt)
 *
 * @auther : GuoXuan
 * @since : 2018/3/9
 */
public class KotlinUtil {

    public static void startWebActivity(String url) {
        CommonWebViewActivityRouter.create(url).route(App.getContext());
    }

    public static NetworkReq get() {
       return NetworkApiDescriptor.MeetingAPI.theme().build();
    }

    public static <E extends Enum<E>, C extends EVal<E>, G extends GroupWallet> Result<G> groupIndex(String text, Class<G> groupClz, E key) throws JSONException {
        Result<G> retResult = new Result<>();

        Class<C> childClz = GenericUtil.getClassType(groupClz);

        Result<C> r = JsonParser.evs(text, childClz);
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

            g.addChild((Wallet) c);
        }

        Collections.sort(mapList, (lhs, rhs) -> lhs.getTag().charAt(0) - rhs.getTag().charAt(0));
        retResult.setData(mapList);
        return retResult;
    }
}
