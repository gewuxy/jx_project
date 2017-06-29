package yy.doctor.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.fitter.DpFitter;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.RecordVH;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.Course.TCourse;

/**
 * 会议记录的Adapter
 *
 * @author : guoxuan
 * @since : 2017/4/26
 */

public class RecordAdapter extends MultiAdapterEx<Course, RecordVH> {

    private int mImgWidth;
    private int mImgHeight;

    public RecordAdapter() {
        mImgWidth = DpFitter.dimen(R.dimen.meeting_record_item_width);
        mImgHeight = DpFitter.dimen(R.dimen.meeting_record_item_height);
    }

    @Override
    public int getConvertViewResId(int itemType) {
        switch (itemType) {
            case CourseType.video:
                return R.layout.layout_meeting_record_video;
            case CourseType.audio:
                return R.layout.layout_meeting_record_audio;
            case CourseType.pic:
                return R.layout.layout_meeting_record_pic;
            case CourseType.pic_audio:
                return R.layout.layout_meeting_record_pic_audio;
        }

        return R.layout.layout_meeting_record_pic;
    }

    @Override
    protected void initView(int position, RecordVH holder, int itemType) {
    }

    @Override
    protected void refreshView(int position, RecordVH holder, int itemType) {
        switch (itemType) {
            case CourseType.video: {
                holder.getIvVideo().res(R.mipmap.meeting_record_ic_video).load();
                setOnViewClickListener(position, holder.getIvVideo());
            }
            break;
            case CourseType.audio: {
                setOnViewClickListener(position, holder.getLayoutAudio());
                animation(position, holder.getIvAudio());
            }
            break;
            case CourseType.pic_audio: {
                // 区别于纯图片的功能
                ImageView iv = holder.getIvPicAudio();
                setOnViewClickListener(position, iv);
                animation(position, iv);
            }
            case CourseType.pic: {
                // 图片,图片+音频共有的功能
                NetworkImageView iv = holder.getIvPic();
                iv.placeHolder(R.mipmap.ic_default_meeting_content_detail)
                        .resize(mImgWidth, mImgHeight)
                        .url(getItem(position).getString(TCourse.imgUrl))
                        .load();
                setOnViewClickListener(position, iv);
            }
            break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return CourseType.class.getDeclaredFields().length;
    }

    private void animation(int position, ImageView iv) {
        AnimationDrawable animation = (AnimationDrawable) iv.getDrawable();
        if (getItem(position).getBoolean(TCourse.play)) {
            animation.start();
        } else {
            animation.stop();
        }
    }

}
