package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ExamEndActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_exam_end;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "考试结束", this);

    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {

    }
}
