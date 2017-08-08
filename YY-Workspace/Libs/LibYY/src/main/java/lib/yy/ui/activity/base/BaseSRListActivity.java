package lib.yy.ui.activity.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import lib.network.model.interfaces.IListResult;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.activity.list.SRListActivityEx;
import lib.ys.util.GenericUtil;
import lib.yy.R;
import lib.yy.network.BaseJsonParser;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.notify.Notifier.OnNotify;



/**
 * @author CaiXiang
 * @since 2017/5/5
 */
abstract public class BaseSRListActivity<T, A extends IAdapter<T>> extends SRListActivityEx<T, A> implements OnNotify {

    private TextView mTvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifier.inst().add(this);

        // 不想影响子类的findView重写
        mTvEmpty = findView(R.id.empty_footer_tv);
        if (mTvEmpty != null) {
            mTvEmpty.setText(getEmptyText());
        }
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
    public IListResult<T> parseNetworkResponse(int id, String text) throws JSONException {
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
