package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.ToastUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.RecordAdapter;
import yy.doctor.util.Util;

/**
 * 会议记录界面
 *
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */

public class MeetingRecordActivity extends BaseListActivity<String, RecordAdapter> {

    private String mTitle;

    public static void nav(Context context, String title) {
        Intent i = new Intent(context, MeetingRecordActivity.class);
        i.putExtra(Extra.KData, title);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mTitle = getIntent().getStringExtra(Extra.KData);

        for (int i = 0; i < 5; i++) {
            addItem("" + i);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_record;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mTitle, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_details, new OnClickListener() {

            @Override
            public void onClick(View v) {
                ToastUtil.makeToast("响应了");
            }
        });
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }
}
