package lib.ys.view.swipeRefresh.footer;

import android.content.Context;
import android.view.View;

import lib.ys.R;
import lib.ys.view.ProgressView;

/**
 * @author yuansui
 */
public class DefaultFooter extends BaseFooter {

    private View mLayoutLoading;
    private View mLayoutReLoadMore;
    private ProgressView mProgressView;

    public DefaultFooter(Context context) {
        super(context);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.sr_list_footer;
    }

    @Override
    protected void findViews() {
        mLayoutLoading = findViewById(R.id.sr_list_footer_layout_loading);
        mLayoutReLoadMore = findViewById(R.id.sr_list_footer_btn_reload_more);
        mProgressView = (ProgressView) findViewById(R.id.sr_list_footer_progress_view);
    }

    @Override
    protected void setViews() {
        setOnRetryClickView(mLayoutReLoadMore);
    }

    @Override
    public void onNormal() {
        showView(mLayoutLoading);
        hideView(mLayoutReLoadMore);
        mProgressView.stop();
    }

    @Override
    public void onReady() {
    }

    @Override
    public void onLoading() {
        showView(mLayoutLoading);
        hideView(mLayoutReLoadMore);
        mProgressView.start();
    }

    @Override
    public void onFailed() {
        hideView(mLayoutLoading);
        showView(mLayoutReLoadMore);
        mProgressView.stop();
    }

    @Override
    public void onFinish() {
        mProgressView.stop();
        hide();
    }
}
