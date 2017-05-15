package yy.doctor.activity;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.view.SideBar;
import lib.yy.activity.base.BaseSRGroupListActivity;
import lib.yy.network.ListResp;
import yy.doctor.R;
import yy.doctor.model.BaseGroup;

/**
 * 带有side bar索引的快速检索group list activity
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
abstract public class BaseGroupIndexActivity<T extends BaseGroup,  A extends IGroupAdapter<T>> extends BaseSRGroupListActivity<T, A> {

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
    public int getSRLayoutResId() {
        return R.id.group_index_list_layout;
    }

    @Override
    public void findViews() {
        super.findViews();

        mSideBar = findView(R.id.group_index_layout_side_bar);
        mTvLetter = findView(R.id.unit_num_tv_letter);

        initSideBar();
    }

    @Override
    public void setViews() {
        super.setViews();

        enableSRRefresh(false);

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
        mSideBar.setOnTouchLetterChangeListener((s, isFocus) -> {
            mTvLetter.setText(s);
            mTvLetter.setVisibility(isFocus ? View.VISIBLE : View.GONE);
            //getListOpt().setSelectedGroup(1);

        });

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp nr) throws Exception {
        ListResp<T> r = (ListResp<T>) super.onNetworkResponse(id, nr);

        if (r.isSucceed()) {
            List<T> data = r.getData();
            mSideBarLetters = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                mSideBarLetters[i] = data.get(i).getTag();
            }
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
