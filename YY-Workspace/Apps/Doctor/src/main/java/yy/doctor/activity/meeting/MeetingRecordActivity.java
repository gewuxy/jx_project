package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.ToastUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.RecordAdapter;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.util.Util;

/**
 * 会议记录界面
 *
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */

public class MeetingRecordActivity extends BaseListActivity<Course, RecordAdapter> {
    // TODO: 2017/6/6 未完
    private PPT mPPT;
    private CourseInfo mCourseInfo;

    public static void nav(Context context, PPT ppt) {
        Intent i = new Intent(context, MeetingRecordActivity.class);
        i.putExtra(Extra.KData, ppt);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mPPT = (PPT) getIntent().getSerializableExtra(Extra.KData);
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
        Util.addBackIcon(bar, mCourseInfo.getString(TCourseInfo.title), this);
        bar.addViewRight(R.mipmap.nav_bar_ic_details, v -> ToastUtil.makeToast("响应了"));
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }
}
