package yy.doctor.adapter.meeting;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.fitter.DpFitter;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.CourseVH;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.Course.TCourse;

/**
 * 录播竖屏中部Recycler的Adapter
 *
 * @auther : GuoXuan
 * @since : 2017/9/29
 */
public class MeetingRepPAdapter extends RecyclerAdapterEx<Course, CourseVH> {

    private final static int KSize = DpFitter.dp(20);

    @Override
    protected void refreshView(int position, CourseVH holder) {
//        NetworkImageView.clearMemoryCache(getContext());
        switch (getItem(position).getType()) {
            case CourseType.video: {
                holder.getIvPPT()
                        .res(R.drawable.ic_default_breviary_video)
                        .load();
            }
            break;
            case CourseType.audio: {
                holder.getIvPPT()
                        .res(R.drawable.ic_default_breviary_audio)
                        .load();
            }
            break;
            case CourseType.pic_audio:
            case CourseType.pic: {
                holder.getIvPPT()
                        .res(R.drawable.ic_default_breviary_image)
                        .url(getItem(position).getString(TCourse.imgUrl))
                        .resize(KSize, KSize)
                        .load();
            }
            break;
        }

        if (getItem(position).getBoolean(TCourse.select)) {
            setView(position);
        } else {
            goneView(holder.getLayoutMedia());
        }

        if (getItem(position).getBoolean(TCourse.play)) {
            nativeAnimation(holder, true);
        } else {
            nativeAnimation(holder, false);
        }
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_ppt_breviary_item_p;
    }

    private void nativeAnimation(CourseVH holder, boolean state) {
        Drawable drawable = holder.getIvMedia().getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) drawable;
            if (state) {
                animation.start();
            } else {
                animation.stop();
            }
        }
    }

    protected void setView(int position) {
        CourseVH holder = getCacheVH(position);
        int type = getItem(position).getType();
        switch (type) {
            case CourseType.audio:
            case CourseType.pic_audio: {
                showView(holder.getLayoutMedia());
                holder.getTvMedia().setText("音频");
                holder.getIvMedia().setImageResource(R.drawable.animation_audio);
            }
            break;
            case CourseType.pic: {
                // do nothing
            }
            break;
            case CourseType.video: {
                showView(holder.getLayoutMedia());
                holder.getTvMedia().setText("视频");
                holder.getIvMedia().setImageResource(R.drawable.breviary_ic_video);
            }
            break;
        }
    }

}
