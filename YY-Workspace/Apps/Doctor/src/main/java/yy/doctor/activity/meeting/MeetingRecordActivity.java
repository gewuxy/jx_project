package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.ToastUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.RecordAdapter;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;

/**
 * 会议记录界面
 * <p>
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */

public class MeetingRecordActivity extends BaseListActivity<Course, RecordAdapter> implements OnAdapterClickListener {
    // TODO: 2017/6/6 未完
    private PPT mPPT;
    private int mSelect;
    private CourseInfo mCourseInfo;

    public static void nav(Context context, PPT ppt, int select) {
        Intent i = new Intent(context, MeetingRecordActivity.class)
                .putExtra(Extra.KData, ppt)
                .putExtra(Extra.KId, select);
        LaunchUtil.startActivityForResult(context, i, 0);
    }

    @Override
    public void initData() {
        mPPT = (PPT) getIntent().getSerializableExtra(Extra.KData);
        mSelect = getIntent().getIntExtra(Extra.KId, 0);
        mCourseInfo = mPPT.getEv(TPPT.course);
        List<Course> courses = mCourseInfo.getList(TCourseInfo.details);
        addAll(courses);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_record;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, mCourseInfo.getString(TCourseInfo.title), v -> {
            // 把前面的页面关了
            notify(NotifyType.finish);
            finish();
        });
        bar.addViewRight(R.mipmap.nav_bar_ic_details, v -> ToastUtil.makeToast("响应了"));
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnAdapterClickListener(this);
        getLv().setSelection(mSelect);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        Intent intent = new Intent()
                .putExtra(Extra.KId, position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
