package yy.doctor.activity.meeting;

import android.support.v4.content.ContextCompat;
import android.view.View;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.ys.util.ToastUtil;
import lib.ys.util.UIUtil;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.R;
import yy.doctor.adapter.RecordAdapter;
import yy.doctor.util.Util;

/**
 * 会议记录界面
 *
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */

public class MeetingRecordActivity extends BaseListActivity<String> {

    private static final int KListViewColor = R.color.divider;
    private String mTitle;
    private int mDividerHeight;

    @Override
    public void initData() {
        mDividerHeight = UIUtil.dpToPx(18.0f, MeetingRecordActivity.this);

        mTitle = "医学研究";

        for (int i = 0; i < 5; i++) {
            addItem("" + i);
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mTitle, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_details, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ToastUtil.makeToast("响应了");
            }
        });
    }

    @Override
    public void findViews() {
        super.findViews();
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        //处理分割线和背景
        getListWidget().getLv().setPadding(0, mDividerHeight, 0, 0);
        getListWidget().getLv().setDivider(ContextCompat.getDrawable(MeetingRecordActivity.this, KListViewColor));
        getListWidget().getLv().setBackgroundResource(KListViewColor);
        getListWidget().setDividerHeight(mDividerHeight);
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new RecordAdapter();
    }
}
