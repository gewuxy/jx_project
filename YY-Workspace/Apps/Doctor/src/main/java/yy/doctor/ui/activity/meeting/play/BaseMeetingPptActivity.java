package yy.doctor.ui.activity.meeting.play;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingBreviaryAdapter;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptContract;
import yy.doctor.ui.frag.meeting.PPTRebFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;
import yy.doctor.ui.frag.meeting.course.VideoCourseFrag;
import yy.doctor.view.discretescrollview.DiscreteScrollView;
import yy.doctor.view.discretescrollview.ScaleTransformer;

/**
 * 观看会议(录播 / PPT直播)
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
abstract public class BaseMeetingPptActivity<V extends MeetingPptContract.View, P extends MeetingPptContract.Presenter>
        extends BaseMeetingPlayActivity
        implements ViewPager.OnPageChangeListener {

    private final int KWhatPlay = 0; // 播放

    private View mLayoutFrag;
    private PPTRebFrag mFragReb;

    private DiscreteScrollView mRvP;

    private P mPresenter;
    private V mView;

    private LayoutParams mParamP;
    private LayoutParams mParamL;

    protected Handler mHandler;

    public PPTRebFrag getFragPpt() {
        return mFragReb;
    }

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case KWhatPlay: {
                        int position = (int) msg.obj;
                        mPresenter.playMedia(position);
                    }
                    break;
                    default: {
                        handler(what);
                    }
                    break;
                }
            }
        };
        mView = createView();
        mPresenter = createPresenter(mView);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void findViews() {
        super.findViews();

        mFragReb = findFragment(R.id.meet_ppt_frag_rep);
        mLayoutFrag = findView(R.id.meet_ppt_layout);

        mRvP = findView(R.id.meet_ppt_rv_p);
    }

    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.embed);
        mPresenter.exeNetworkReq(MeetAPI.toCourse(mMeetId, mModuleId).build());

        mFragReb.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing
    }

    @Override
    public void onPageSelected(int position) {
        NetworkImageView.clearMemoryCache(BaseMeetingPptActivity.this);

        mHandler.removeMessages(KWhatPlay);
        Message message = Message.obtain();
        message.obj = position;
        message.what = KWhatPlay;
        mHandler.sendMessageDelayed(message, 300);
        setTextCur(position + 1);
        mRvP.smoothScrollToPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }

    /**
     * 子类处理的消息
     */
    protected void handler(int what) {
        // do nothing
    }

    @Override
    protected void portrait() {
        mFragReb.landscapeVisibility(false);
        showView(R.id.meet_ppt_layout_p);

        if (mParamP == null) {
            mParamP = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, (int) ResLoader.getDimension(R.dimen.meet_play_ppt));
        }
        mLayoutFrag.setLayoutParams(mParamP);
        mView.finishCount();
    }

    @Override
    protected void landscape() {
        mFragReb.landscapeVisibility(true);
        goneView(R.id.meet_ppt_layout_p);

        if (mParamL == null) {
            mParamL = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        }
        mLayoutFrag.setLayoutParams(mParamL);
        showLandscapeView();
    }

    @Override
    protected void toggle() {
        mPresenter.toggle();
    }

    @Override
    protected void toLeft() {
        mFragReb.offsetPosition(-1, getString(R.string.course_first));
    }

    @Override
    protected void toRight() {
        mFragReb.offsetPosition(1, getString(R.string.course_last));
    }

    @Override
    protected void showLandscapeView() {
        mPresenter.starCount();
        showView(getNavBar());
        mFragReb.landscapeVisibility(true);
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            mPresenter.exeNetworkReq(MeetAPI.toCourse(mMeetId, mModuleId).build());
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenter.stopMedia();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);
        mPresenter.onDestroy();
    }

    abstract protected V createView();

    abstract protected P createPresenter(V v);

    protected class MeetingPptViewImpl extends BaseViewImpl implements MeetingPptContract.View {

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            // 初始显示
            setViewState(ViewState.normal);
            mFragReb.setPPT(ppt);
            mFragReb.addCourses();

            setTextComment(ppt.getInt(TPPT.count));
            CourseInfo courseInfo = ppt.get(TPPT.course);
            setTextTitle(courseInfo.getString(TCourseInfo.title));

            MeetingBreviaryAdapter adapter = new MeetingBreviaryAdapter();
            adapter.setData(courses);
            adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

                @Override
                public void onItemClick(View v, int position) {
                    mFragReb.setCurrPosition(position);
                }

            });
            mRvP.setAdapter(adapter);
            mRvP.setSlideOnFling(true);
            mRvP.setItemTransitionTimeMillis(150);
            mRvP.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mPresenter.playMedia(0);
                    removeOnGlobalLayoutListener(this);
                }

            });
            setTextAll(courses.size());
            setTextCur(1);
        }

        @Override
        public void onPlayState(boolean state) {
            setPlayState(state);
        }

        @Override
        public PLVideoTextureView getTextureView() {
            BaseCourseFrag f = mFragReb.getItem(mFragReb.getCurrPosition());
            if (f instanceof VideoCourseFrag) {
                VideoCourseFrag item = (VideoCourseFrag) f;
                return item.getTextureView();
            }
            return null;
        }

        @Override
        public void invalidate(int position) {
            RecyclerAdapterEx p = (RecyclerAdapterEx) mRvP.getAdapter();
            p.invalidate(position);
        }

        @Override
        public void setNextItem() {
            toRight();
        }

        @Override
        public void finishCount() {
            BaseMeetingPptActivity.this.goneView(getNavBar());
            mFragReb.landscapeVisibility(false);
        }

        @Override
        public void setTextOnline(int onlineNum) {
            BaseMeetingPptActivity.this.setTextOnline(onlineNum);
        }

    }
}