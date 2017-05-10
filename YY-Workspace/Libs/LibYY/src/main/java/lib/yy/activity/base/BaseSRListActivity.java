package lib.yy.activity.base;

import android.os.Bundle;

import org.json.JSONException;

import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.network.resp.IListResponse;
import lib.ys.ui.activity.list.SRListActivityEx;
import lib.ys.util.GenericUtil;
import lib.yy.Notifier;
import lib.yy.Notifier.NotifyType;
import lib.yy.Notifier.OnNotify;
import lib.yy.network.BaseJsonParser;

/**
 * @author CaiXiang
 * @since 2017/5/5
 */
abstract public class BaseSRListActivity<T, A extends IAdapter<T>> extends SRListActivityEx<T, A> implements OnNotify {

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

    protected void notify(@NotifyType int type, Object data) {
        Notifier.inst().notify(type, data);
    }

    protected void notify(@NotifyType int type) {
        Notifier.inst().notify(type);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
    }

    @Override
    public IListResponse<T> parseNetworkResponse(int id, String text) throws JSONException {
        return BaseJsonParser.evs(text, GenericUtil.getClassType(getClass()));
    }
}
