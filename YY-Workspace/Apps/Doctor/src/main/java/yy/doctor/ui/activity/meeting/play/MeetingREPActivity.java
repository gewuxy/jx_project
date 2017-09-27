package yy.doctor.ui.activity.meeting.play;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.model.meet.Submit;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.serv.CommonServRouter;
import yy.doctor.ui.frag.meeting.OnPPTChangeListener;
import yy.doctor.ui.frag.meeting.PPTBreviaryFrag;
import yy.doctor.ui.frag.meeting.PPTRepFrag;
import yy.doctor.util.NetPlayer;

/**
 * 观看会议(录播)
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
@Route
public class MeetingREPActivity extends BaseMeetingPlayActivity implements OnPPTChangeListener, MeetingREPContract.View {

    private PPTBreviaryFrag mFragBreviary; // 缩略图
    private PPTRepFrag mFragRepP; //
    private PPTRepFrag mFragRepL;
    private TextView mTvCurrent;

    private TextView mTvAll;
    private MeetingREPContract.Presenter mPresenter;
    private PPT mPPT;

    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mPresenter = new MeetingREPPresenterImpl(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void findViews() {
        super.findViews();

        mFragBreviary = findFragment(R.id.meet_ppt_frag_breviary);
        mFragRepP = findFragment(R.id.meet_ppt_frag_rep_p);
        mFragRepL = findFragment(R.id.meet_ppt_frag_rep_l);

        mTvCurrent = findView(R.id.meet_play_tv_current);
        mTvAll = findView(R.id.meet_play_tv_all);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.meet_play_iv_left);
        setOnClickListener(R.id.meet_play_iv_right);

        refresh(RefreshWay.embed);
        getDataFromNet();
    }

    @Override
    protected void portrait() {
        goneView(R.id.meet_ppt_layout_l);
        showView(R.id.meet_ppt_layout_p);
    }

    @Override
    protected void landscape() {
        goneView(R.id.meet_ppt_layout_p);
        showView(R.id.meet_ppt_layout_l);
        mFragRepL.setPPT(mPPT);
        mPresenter.createAdapter();
    }

    private void getDataFromNet() {
        exeNetworkReq(MeetAPI.toCourse(mMeetId, mModuleId).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), PPT.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<PPT> r = (Result<PPT>) result;
        if (r.isSucceed()) {
            mPPT = r.getData();
            if (mPPT == null) {
                return;
            }
            CourseInfo courseInfo = mPPT.getEv(TPPT.course);
            if (courseInfo == null) {
                return;
            }

            List<Course> courses = courseInfo.getList(TCourseInfo.details);
            if (courses == null || courses.size() == 0) {
                return;
            }

            setViewState(ViewState.normal);

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    // 初始显示
                    mFragRepP.setPPT(mPPT);
                    mFragRepP.setListener(MeetingREPActivity.this);
                    mFragBreviary.setCourses(courses);
                    mFragBreviary.setListener(MeetingREPActivity.this);

                    mTvAll.setText(String.valueOf(courses.size()));
                    mTvCurrent.setText("1");

                    getNavBar().addTextViewMid(courseInfo.getString(TCourseInfo.title));

                    removeOnGlobalLayoutListener(this);
                }

            });

        } else {
            showToast(r.getMessage());
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            getDataFromNet();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meet_play_iv_left: {
                // 上一页
                int preItem = mFragBreviary.getCurrentItem() - 1;
                if (preItem >= 0) {
                    onPageSelected(preItem);
                } else {
                    showToast("这是第一页喔");
                }
            }
            break;
            case R.id.meet_play_iv_right: {
                // 下一页
                int currNext = mFragBreviary.getCurrentItem() + 1;
                if (currNext < mFragBreviary.getCount()) {
                    onPageSelected(currNext);
                } else {
                    showToast("已是最后一页");
                }
            }
            break;
            default: {
                super.onClick(v);
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        notify(NotifyType.study_end);

        // 保持调用顺序
        super.onDestroy();

        NetPlayer.inst().recycle();

        Submit submit = mFragRepP.getSubmit();
        if (submit == null) {
            return;
        }

        // 把需要的对象传给服务提交(失败再次提交)
        CommonServRouter.create()
                .type(ReqType.course)
                .submit(submit)
                .route(this);

    }

    @Override
    public void onPageSelected(int position) {
        if (mFragRepP.getCurrentItem() != position) {
            mFragRepP.setCurrentItem(position);
        }
        if (mFragBreviary.getCurrentItem() != position) {
            mFragBreviary.setCurrentItem(position);
        }
        mTvCurrent.setText(String.valueOf(position + 1));
    }
}