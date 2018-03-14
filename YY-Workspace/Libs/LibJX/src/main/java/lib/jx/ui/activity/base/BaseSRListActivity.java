package lib.jx.ui.activity.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import lib.network.model.interfaces.IResult;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.activity.list.SRListActivityEx;
import lib.ys.util.GenericUtil;
import lib.jx.R;
import lib.jx.network.BaseJsonParser;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.notify.Notifier.OnNotify;


/**
 * @author CaiXiang
 * @since 2017/5/5
 */
abstract public class BaseSRListActivity<T, A extends IAdapter<T>> extends SRListActivityEx<T, A> implements OnNotify {

    private TextView mTvEmpty;
    private ImageView mIvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifier.inst().add(this);

        // 不想影响子类的findView重写
        mTvEmpty = findView(R.id.empty_footer_tv);
        mIvEmpty = findView(R.id.empty_footer_iv);
        if (mTvEmpty != null) {
            mTvEmpty.setText(getEmptyText());
        }
        if (mIvEmpty != null) {
            mIvEmpty.setImageResource(getEmptyImg());
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

    protected int getEmptyImg() {
        return R.drawable.ic_empty;
    }
}
