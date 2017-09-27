package yy.doctor.ui.frag.meeting.course;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;
import yy.doctor.R;
import yy.doctor.model.meet.Course.CourseType;

/**
 * 每一张缩略图
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class BreviaryFrag extends BaseFrag {

    @CourseType
    @Arg
    int mType;

    @Arg
    String mUrl;

    private View mLayout;
    private NetworkImageView mIvBreviary;
    private ImageView mIvMedia;
    private TextView mTv;

    @Override
    public void initData() {
        // do nothing
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_ppt_breviary_item;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void findViews() {
        mLayout = findView(R.id.breviary_layout_media);
        mIvBreviary = findView(R.id.breviary_iv_ppt);
        mIvMedia = findView(R.id.breviary_iv_media);
        mTv = findView(R.id.breviary_tv_media);
    }

    @Override
    public void setViews() {
        mIvBreviary.url(mUrl).load();
        switch (mType) {
            case CourseType.audio:
            case CourseType.pic_audio: {
                showView(mLayout);
                mTv.setText("音频");
                mIvMedia.setBackgroundResource(R.drawable.animation_audio);
            }
            break;
            case CourseType.pic: {
                goneView(mLayout);
            }
            break;
            case CourseType.video: {
                showView(mLayout);
                mTv.setText("视频");
                mIvMedia.setBackgroundResource(R.drawable.breviary_ic_video);
            }
            break;
        }
    }

    /**
     * 开启动画
     */
    public void start() {
        switch (mType) {
            case CourseType.audio:
            case CourseType.pic_audio: {
                AnimationDrawable animation = (AnimationDrawable) mIvMedia.getDrawable();
                animation.start();
            }
            break;
        }
    }

    /**
     * 结束动画
     */
    public void stop() {
        switch (mType) {
            case CourseType.audio:
            case CourseType.pic_audio: {
                AnimationDrawable animation = (AnimationDrawable) mIvMedia.getDrawable();
                animation.stop();
            }
            break;
        }
    }
}
