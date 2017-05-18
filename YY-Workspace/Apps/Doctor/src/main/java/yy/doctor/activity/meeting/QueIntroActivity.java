package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import yy.doctor.Extra;
import yy.doctor.model.exam.Intro;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 问卷介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class QueIntroActivity extends BaseIntroActivity {

    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, QueIntroActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mMeetId = intent.getStringExtra(Extra.KMeetId);
        mModuleId = intent.getStringExtra(Extra.KModuleId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "问卷", this);
    }

    @Override
    public void setViews() {
        super.setViews();
        exeNetworkReq(NetFactory.toSurvey(mMeetId, mModuleId));
    }

    @Override
    protected void success(Intro intro) {

    }
}
