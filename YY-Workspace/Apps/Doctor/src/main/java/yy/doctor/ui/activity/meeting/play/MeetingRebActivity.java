package yy.doctor.ui.activity.meeting.play;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Route;
import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptContract;
import yy.doctor.ui.activity.meeting.play.presenter.MeetingPptPresenterImpl;

/**
 * 录播界面
 *
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
@Route
public class MeetingRebActivity extends BaseMeetingPptActivity<MeetingPptContract.View, MeetingPptContract.Presenter> {

    private final int KWhatPass = 1; // 下一页

    @Override
    public void setViews() {
        super.setViews();

        goneView(R.id.meet_play_layout_online);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        if (TextUtil.isEmpty(getFragPpt().getItem(position).getUrl())) {
            mHandler.removeMessages(KWhatPass);
            mHandler.sendEmptyMessageDelayed(KWhatPass, TimeUnit.SECONDS.toMillis(3));
        }
    }

    @Override
    protected void handler(int what) {
        switch (what) {
            case KWhatPass: {
                getFragPpt().setCurrentItem(1, getString(R.string.course_last));
            }
            break;
        }
    }

    @Override
    public MeetingPptContract.View createView() {
        return new MeetingPptViewImpl();
    }

    @Override
    public MeetingPptContract.Presenter createPresenter(MeetingPptContract.View view) {
        return new MeetingPptPresenterImpl(view);
    }

}
