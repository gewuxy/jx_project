package lib.yy.frag.base;

import org.json.JSONException;

import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.network.resp.IListResp;
import lib.ys.ui.frag.list.SRListFragEx;
import lib.ys.util.GenericUtil;
import lib.yy.Notifier;
import lib.yy.Notifier.NotifyType;
import lib.yy.Notifier.OnNotify;
import lib.yy.network.BaseJsonParser;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
abstract public class BaseSRListFrag<T, A extends IAdapter<T>> extends SRListFragEx<T, A> implements OnNotify {

    @Override
    protected void afterInitCompleted() {
        Notifier.inst().add(this);
    }

    @Override
    public void onDestroy() {
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
