package yy.doctor.activity;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.view.SideBar;
import lib.yy.network.ListResult;
import lib.yy.ui.activity.base.BaseSRGroupListActivity;
import yy.doctor.R;
import yy.doctor.model.BaseGroup;

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

    private SideBar mSideBar;
    private TextView mTvLetter;

    private String[] mSideBarLetters;

    @Override
    public void initData() {
        mLetterSize = fitDp(10);
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

        initSideBar();
    }

    @Override
    public void setViews() {
        super.setViews();

        enableSRRefresh(false);
        enableAutoLoadMore(false);

        mSideBar.setTextSize(fitDp(10));
        mSideBar.setSingleHeight(fitDp(15));
        mSideBar.setGravity(Gravity.CENTER);
    }

    /**
     * 初始化SideBar
     */
    private void initSideBar() {

        mSideBar.setTextSize(mLetterSize);
        mSideBar.setColor(KLetterColorNormal);
        mSideBar.setColorFocus(KLetterColorFocus);
        mSideBar.setOnTouchLetterChangeListener((index, isFocus) -> {
            mTvLetter.setText(mSideBarLetters[index]);
            mTvLetter.setVisibility(isFocus ? View.VISIBLE : View.GONE);
            setSelectedGroup(index);
        });

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp nr) throws Exception {
        ListResult<GROUP> r = (ListResult<GROUP>) super.onNetworkResponse(id, nr);

        if (r.isSucceed()) {
            List<GROUP> data = r.getData();
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
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        mSideBar.setData(mSideBarLetters);
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();

        expandAllGroup();
    }

}
