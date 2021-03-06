package jx.doctor.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.view.SideBar;
import lib.jx.ui.activity.base.BaseSRGroupListActivity;
import jx.doctor.R;
import jx.doctor.model.BaseGroup;

/**
 * 带有side bar索引的快速检索group list activity
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
abstract public class BaseGroupIndexActivity<GROUP extends BaseGroup<CHILD>, CHILD, A extends IGroupAdapter<GROUP, CHILD>>
        extends BaseSRGroupListActivity<GROUP, CHILD, A> {

    private static final int KLetterColorNormal = Color.parseColor("#888888");
    private static final int KLetterColorFocus = Color.parseColor("#0882e7");

    private int mLetterSize;
    private int mSingleLetterHight;

    private SideBar mSideBar;
    private TextView mTvLetter;

    private String[] mSideBarLetters;

    @Override
    public void initData() {
        mLetterSize = fit(10);
        mSingleLetterHight = fit(15);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_group_index;
    }

    @Override
    public void findViews() {
        super.findViews();

        mSideBar = findView(R.id.group_index_layout_side_bar);
        mTvLetter = findView(R.id.group_index_tv_letter);
    }

    @Override
    public void setViews() {
        super.setViews();

        setRefreshEnabled(false);
        setAutoLoadMoreEnabled(false);

        initSideBar();
    }

    /**
     * 初始化SideBar
     */
    private void initSideBar() {

        mSideBar.setTextSize(mLetterSize);
        mSideBar.setSingleHeight(mSingleLetterHight);
        mSideBar.setGravity(Gravity.CENTER);
        mSideBar.setColor(KLetterColorNormal);
        mSideBar.setColorFocus(KLetterColorFocus);
        mSideBar.setOnTouchLetterChangeListener((index, s, isFocus) -> {
            mTvLetter.setText(mSideBarLetters[index]);
            mTvLetter.setVisibility(isFocus ? View.VISIBLE : View.GONE);
            setSelectedGroup(index);
        });
    }

    @CallSuper
    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        IResult<GROUP> r = super.onNetworkResponse(id, resp);

        if (r.isSucceed()) {
            List<GROUP> data = r.getList();
            if (data == null) {
                return r;
            }

            mSideBarLetters = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                mSideBarLetters[i] = data.get(i).getTag();
            }
            Arrays.sort(mSideBarLetters);
        }

        return r;
    }

    @Override
    public void onNetRefreshSuccess() {
        super.onNetRefreshSuccess();
        mSideBar.setData(mSideBarLetters);
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();

        expandAllGroup();
    }

}
