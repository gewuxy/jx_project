package jx.csp.ui.activity.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Square;
import jx.csp.model.main.Square.TSquare;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivityRouter;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;

/**
 * @auther WangLan
 * @since 2017/10/18
 */
@Route
public class MainMeetingFrag extends BaseFrag {

    private NetworkImageView mIvCover;
    private ImageView mIvLive;
    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvState;
    private View mVDivider;
    private View mLayout;

    @Arg
    Square mSquare;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_main_slide;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mIvCover = findView(R.id.iv_main_cover);
        mTvTitle = findView(R.id.tv_title);
        mIvLive = findView(R.id.iv_live);
        mTvTime = findView(R.id.tv_total_time);
        mTvCurrentPage = findView(R.id.tv_current_page);
        mTvTotalPage = findView(R.id.tv_total_page);
        mTvState = findView(R.id.tv_state);
        mVDivider = findView(R.id.slide_divider);
    }

    @Override
    public void setViews() {
        mIvCover.placeHolder(R.drawable.ic_default_record)
                .url(mSquare.getString(TSquare.coverUrl))
                .load();
        mTvTitle.setText(mSquare.getString(TSquare.title));
        mTvTotalPage.setText(mSquare.getString(TSquare.pageCount));

        if (mSquare.getInt(TSquare.playType) == PlayType.reb) {
            mTvTime.setText(mSquare.getString(TSquare.playTime));
            mTvCurrentPage.setText(mSquare.getString(TSquare.playPage));

            if (mSquare.getInt(TSquare.playState) == PlayState.un_start) {
                mTvState.setText(R.string.record);
            }else if (mSquare.getInt(TSquare.playState) == PlayState.record) {
                mTvState.setText("录播中");
            }else {
               goneView(mTvCurrentPage);
               goneView(mVDivider);
                goneView(mTvState);
            }
            goneView(mIvLive);
        }else {
            //直播的开始时间转换
            Date d = new Date(Long.parseLong(mSquare.getString(TSquare.startTime)));
            SimpleDateFormat data = new SimpleDateFormat("MM月dd日 HH:mm");
            mTvTime.setText(data.format(d));
            mTvCurrentPage.setText(mSquare.getString(TSquare.livePage));

            if (mSquare.getInt(TSquare.liveState) == LiveState.un_start) {
                mTvState.setText(R.string.solive);
            }else if (mSquare.getInt(TSquare.liveState) == LiveState.live) {
                mTvState.setText("直播中");
            }else {
                goneView(mTvCurrentPage);
                goneView(mVDivider);
                goneView(mTvState);
                goneView(mIvLive);
            }
        }

        setOnClickListener(R.id.iv_share);
        setOnClickListener(R.id.main_slide_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_share:{
                //Fixme:传个假的url
                ShareDialog shareDialog = new ShareDialog(getContext(), "http://blog.csdn.net/happy_horse/article/details/51164262", "哈哈");
                shareDialog.show();
            }
            break;
            case R.id.main_slide_layout: {
                String courseId = mSquare.getString(TSquare.id);
                if (mSquare.getInt(TSquare.playType) == PlayType.reb) {
                    CommonRecordActivityRouter.create(courseId).route(getContext());
                } else {
                    LiveRecordActivityRouter.create(courseId).route(getContext());
                }
            }
            break;

        }
    }
}
