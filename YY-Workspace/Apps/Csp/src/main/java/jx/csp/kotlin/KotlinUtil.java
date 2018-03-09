package jx.csp.kotlin;

import jx.csp.App;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import lib.network.model.NetworkReq;

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
}
