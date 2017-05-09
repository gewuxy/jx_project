package yy.doctor.activity.me;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.ys.ui.other.NavBar;
import lib.ys.view.SideBar;
import lib.yy.activity.base.BaseSRGroupListActivity;
import yy.doctor.R;
import yy.doctor.adapter.UnitNumAdapter;
import yy.doctor.model.unitnum.GroupUnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 单位号列表
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class UnitNumActivity extends BaseSRGroupListActivity<GroupUnitNum> {

    private static final int KLetterColorNormal = Color.parseColor("#888888");
    private static final int KLetterColorFocus = Color.parseColor("#0882e7");

    private int mLetterSize;

    private SideBar mSideBar;
    private TextView mTvLetter;


    @Override
    public void initData() {

        mLetterSize = fitDp(10);

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_unit_num;
    }

    @Override
    public int getSRLayoutResId() {
        return R.id.unit_num_sr_group_list_layout;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "单位号", this);
        bar.addViewRight(R.mipmap.nav_bar_ic_add, new OnClickListener() {

            @Override
            public void onClick(View v) {
                showToast("852");
            }
        });

    }

    @Override
    public void findViews() {
        super.findViews();

        mSideBar = findView(R.id.unit_num_side_bar);
        mTvLetter = findView(R.id.unit_num_tv_letter);

        initSideBar();
    }

    @Override
    public void setViews() {
        super.setViews();

        setRefreshEnable(false);

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
            //getListWidget().setSelectedGroup(1);

        });

    }

    @Override
    public MultiGroupAdapterEx<GroupUnitNum, ? extends ViewHolderEx> createAdapter() {
        return new UnitNumAdapter();
    }

    @Override
    public void getDataFromNet() {
        exeNetworkRequest(0, NetFactory.unitNum());
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        startActivity(UnitNumDetailActivity.class);

        return true;
    }

    @Override
    public IListResponse<GroupUnitNum> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.unitNums(text);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        IListResponse<GroupUnitNum> listResponse = (IListResponse<GroupUnitNum>) result;
        List<GroupUnitNum> data = listResponse.getData();
        String[] str = new String[data.size()];
        if (listResponse.isSucceed()) {
            for (int i = 0; i < data.size(); i++) {
                str[i] = data.get(i).getLetter();
            }
        }
        mSideBar.setData(str);
        mSideBar.invalidate();
    }

}
