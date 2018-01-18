package lib.jx.ui.activity.base;

import android.os.Bundle;

import lib.jx.network.BaseJsonParser;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.notify.Notifier.OnNotify;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.activity.ActivityEx;

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

