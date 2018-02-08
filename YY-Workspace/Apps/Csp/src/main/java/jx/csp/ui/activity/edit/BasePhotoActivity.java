package jx.csp.ui.activity.edit;

import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import jx.csp.R;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.interfaces.IAdapter;

/**
 * @auther : GuoXuan
 * @since : 2018/2/5
 */
abstract public class BasePhotoActivity<T, A extends IAdapter<T>> extends BaseRecyclerActivity<T, A> implements
        MultiAdapterEx.OnAdapterClickListener {

    private TextView mTvLeft;
    private TextView mTvRight;

    protected TextView getLeftButton() {
        return mTvLeft;
    }

    protected TextView getRightButton() {
        return mTvRight;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_photo;
    }

    @Override
    public void initData() {
        // do nothing
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvLeft = findView(R.id.photo_tv_bottom_left);
        mTvRight = findView(R.id.photo_tv_bottom_right);
    }

    @Override
    public void setViews() {
        super.setViews();

        getDecorView().setBackgroundResource(R.color.text_f2f2f4);
        setOnClickListener(mTvLeft);
        setOnClickListener(mTvRight);

        setOnAdapterClickListener(this);
    }

    @Override
    protected RecyclerView.LayoutManager initLayoutManager() {
        return new GridLayoutManager(BasePhotoActivity.this, getSpanCount());
    }

    protected void setLeftText(@StringRes int resId) {
        mTvLeft.setText(resId);
    }

    protected void setRightText(@StringRes int resId) {
        mTvRight.setText(resId);
    }

    /**
     * @return 列数
     */
    abstract protected int getSpanCount();

}
