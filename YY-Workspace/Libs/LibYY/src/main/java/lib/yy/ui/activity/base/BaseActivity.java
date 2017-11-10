package lib.yy.ui.activity.base;

import android.os.Bundle;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.activity.ActivityEx;
import lib.yy.network.BaseJsonParser;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.notify.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017.3.30
 */
abstract public class BaseActivity extends ActivityEx implements OnNotify {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifier.inst().add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Notifier.inst().remove(this);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return BaseJsonParser.error(resp.getText());
    }

    protected void notify(@NotifyType int type, Object data) {
        Notifier.inst().notify(type, data);
    }

    protected void notify(@NotifyType int type) {
        Notifier.inst().notify(type);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
    }
}

