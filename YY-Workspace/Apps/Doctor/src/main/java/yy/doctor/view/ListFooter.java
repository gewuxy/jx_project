package yy.doctor.view;

import android.content.Context;

import lib.ys.view.swipeRefresh.footer.BaseFooter;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/18
 */

public class ListFooter extends BaseFooter {

    public ListFooter(Context context) {
        super(context);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.layout_home_footer;
    }

    @Override
    protected void findViews() {
    }

    @Override
    protected void setViews() {
    }

    @Override
    public void onNormal() {
    }

    @Override
    public void onReady() {
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onFinish() {

    }
}
