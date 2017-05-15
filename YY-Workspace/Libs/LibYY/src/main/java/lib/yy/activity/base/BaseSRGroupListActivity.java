package lib.yy.activity.base;

import android.os.Bundle;

import org.json.JSONException;

import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.network.resp.IListResp;
import lib.ys.ui.activity.list.SRGroupListActivityEx;
import lib.ys.util.GenericUtil;
import lib.yy.Notifier;
import lib.yy.Notifier.NotifyType;
import lib.yy.Notifier.OnNotify;
import lib.yy.network.BaseJsonParser;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
abstract public class BaseSRGroupListActivity<T, A extends IGroupAdapter<T>> extends SRGroupListActivityEx<T, A> implements OnNotify {

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
    public IListResp<T> parseNetworkResponse(int id, String text) throws JSONException {
        return BaseJsonParser.evs(text, GenericUtil.getClassType(getClass()));
    }
}
