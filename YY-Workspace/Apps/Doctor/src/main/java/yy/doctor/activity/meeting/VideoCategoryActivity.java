package yy.doctor.activity.meeting;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.adapter.meeting.VideoCategoryAdapter;
import yy.doctor.model.meet.video.Intro;
import yy.doctor.util.Util;

/**
 * 视频列表(大分类)界面
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class VideoCategoryActivity extends BaseListActivity<Intro, VideoCategoryAdapter> {

    @Override
    public void initData() {
        for (int i = 0; i < 10; i++) {
            addItem(new Intro());
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "视频", this);
    }

}
