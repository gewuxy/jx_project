package lib.jx.ui.frag.base;

import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import lib.network.model.interfaces.IResult;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.frag.list.SRRecyclerFragEx;
import lib.ys.util.GenericUtil;
import lib.jx.R;
import lib.jx.network.BaseJsonParser;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.notify.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */

abstract public class BaseSRRecyclerFrag<T, A extends IAdapter<T>> extends SRRecyclerFragEx<T, A> implements OnNotify {
    private TextView mTvEmpty;

    @Override
    protected void afterInitCompleted() {
        Notifier.inst().add(this);

        // 不想影响子类的findView重写
        mTvEmpty = findView(R.id.empty_footer_tv);
        if (mTvEmpty != null) {
            mTvEmpty.setText(getEmptyText());
        }
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

    public void onNotify(@NotifyType int type, Object data) {
    }

    @Override
    public IResult parseNetworkResponse(int id, String text) throws JSONException {
        return BaseJsonParser.evs(text, GenericUtil.getClassType(getClass()));
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_footer);
    }

    protected String getEmptyText() {
        return "暂时没有相关内容";
    }

}
