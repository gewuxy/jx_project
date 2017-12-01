package lib.jx.ui.activity.base;

import android.os.Bundle;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.activity.form.FormActivityEx;
import lib.jx.adapter.VH.FormVH;
import lib.jx.model.form.BaseForm;
import lib.jx.network.BaseJsonParser;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.notify.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017/4/12
 */
abstract public class BaseFormActivity extends FormActivityEx<BaseForm, FormVH> implements OnNotify {

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
