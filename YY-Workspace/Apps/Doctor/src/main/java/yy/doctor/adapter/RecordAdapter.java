package yy.doctor.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.ImageView;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.network.image.NetworkImageView;
import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.RecordVH;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.TCourse;

/**
 * 会议记录的Adapter
 *
 * @author : guoxuan
 * @since : 2017/4/26
 */

public class RecordAdapter extends MultiAdapterEx<Course, RecordVH> {

    private static final int KPicBack = R.mipmap.ic_default_meeting_content_detail; // 默认图

    private String mImgUrl;
    private String mAudioUrl;
    private String mVideoUrl;
    private int mLastPosition; // 上一个PicAudio的position
    private AnimationDrawable mLastAnimation; // 上一个PicAudio的动画

    @IntDef({
            RecordType.video,
            RecordType.audio,
            RecordType.pic,
            RecordType.pic_audio,
    })
    private @interface RecordType {
        int video = 0;
        int audio = 1;
        int pic = 2;
        int pic_audio = 3;
    }

    @Override
    public int getConvertViewResId(int itemType) {
        switch (itemType) {
            case RecordType.video:
                return R.layout.layout_meeting_record_video;
            case RecordType.audio:
                return R.layout.layout_meeting_record_audio;
            case RecordType.pic:
                return R.layout.layout_meeting_record_pic;
            case RecordType.pic_audio:
                return R.layout.layout_meeting_record_pic_audio;
        }
        return -1;
    }

    @Override
    protected void initView(int position, RecordVH holder, int itemType) {
        switch (itemType) {
            case RecordType.audio:
                break;
            case RecordType.pic_audio: {
                AnimationDrawable animation = (AnimationDrawable) holder.getIvPicAudio().getDrawable();
                animation.stop();
            }
            break;
        }
    }

    @Override
    protected void refreshView(int position, RecordVH holder, int itemType) {
        switch (itemType) {
            case RecordType.video:
                break;
            case RecordType.audio:
                break;
            case RecordType.pic_audio: {
                // 区别于纯图片的功能
                setOnViewClickListener(position, holder.getIvPicAudio());
            }
            case RecordType.pic: {
                // 图片,图片+音频共有的功能
                NetworkImageView iv = holder.getIvPic();
                iv.placeHolder(KPicBack).url(mImgUrl).load();
                setOnViewClickListener(position, iv);
            }
            break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        mImgUrl = getItem(position).getString(TCourse.imgUrl);
        mAudioUrl = getItem(position).getString(TCourse.audioUrl);
        mVideoUrl = getItem(position).getString(TCourse.videoUrl);

        if (!TextUtil.isEmpty(mVideoUrl)) {
            // 有视频
            return RecordType.video;
        } else if (!TextUtil.isEmpty(mAudioUrl)) {
            // 有音频
            if (!TextUtil.isEmpty(mImgUrl)) {
                // 有音频且有图片
                return RecordType.pic_audio;
            } else {
                // 只有音频
                return RecordType.audio;
            }
        } else {
            // 只有图片
            return RecordType.pic;
        }
    }

    @Override
    public int getViewTypeCount() {
        return RecordType.class.getDeclaredFields().length;
    }

    @Override
    protected void onViewClick(int position, View v) {
        if (v instanceof NetworkImageView) {

        } else if (v instanceof ImageView) {
            AnimationDrawable animation = (AnimationDrawable) getCacheVH(position).getIvPicAudio().getDrawable();
            if (mLastPosition != position) {
                animation.start();
            } else {
                animation.stop();
            }
            if (mLastAnimation != null) {
                mLastAnimation.stop();
            } else {
                mLastAnimation = animation;
            }
        }
    }
}
