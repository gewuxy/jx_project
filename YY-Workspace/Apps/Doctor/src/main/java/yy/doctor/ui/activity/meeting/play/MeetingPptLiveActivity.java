package yy.doctor.ui.activity.meeting.play;

import inject.annotation.router.Route;

/**
 * ppt直播(无视频)
 *
 * @auther : GuoXuan
 * @since : 2017/10/27
 */
@Route
public class MeetingPptLiveActivity extends MeetingRebActivity {

    @Override
    protected void set() {

    }

    private class MeetingPptLiveViewImpl extends BaseViewImpl implements MeetingPptLiveContract.View {}
}
