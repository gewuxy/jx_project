package jx.doctor.adapter.meeting;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.fitter.Fitter;
import lib.ys.network.image.NetworkImageView;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.CourseVH;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.CourseType;
import jx.doctor.model.meet.ppt.Course.TCourse;

/**
 * 录播ppt缩略图的Apadter
 *
 * @auther : GuoXuan
 * @since : 2017/9/29
 */
public class PptBreviaryAdapter extends RecyclerAdapterEx<Course, CourseVH> {

    private final int KW = 129;
    private final int KH = 96;

    @Override
    protected void refreshView(int position, CourseVH holder) {
        int type = getItem(position).getType();
        NetworkImageView ivPPT = holder.getIvPPT();
        switch (type) {
            case CourseType.video: {
                ivPPT.res(R.drawable.ic_default_breviary_video)
                        .url(getItem(position).getString(TCourse.imgUrl))
                        .resize(Fitter.dp(KW), Fitter.dp(KH))
                        .load();
            }
            break;
            case CourseType.audio: {
                ivPPT.res(R.drawable.ic_default_breviary_audio).load();
            }
            break;
            case CourseType.pic_audio:
            case CourseType.pic: {
                ivPPT.res(R.drawable.ic_default_breviary_image)
                        .url(getItem(position).getString(TCourse.imgUrl))
                        .resize(Fitter.dp(KW), Fitter.dp(KH))
                        .load();
            }
            break;
        }

        if (getItem(position).getBoolean(TCourse.select)) {
            setView(position);
        } else {
            goneView(holder.getLayoutMedia());
        }

        if (getItem(position).getBoolean(TCourse.play) && type != CourseType.video) {
            nativeAnimation(holder, true);
        } else {
            nativeAnimation(holder, false);
        }
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_ppt_breviary_item;
    }

    private void nativeAnimation(CourseVH holder, boolean state) {
        Drawable drawable = holder.getIvMedia().getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) drawable;
            if (state == animation.isRunning()) {
                return;
            }
            if (state) {
                animation.start();
            } else {
                animation.stop();
            }
        }
    }

    private void setView(int position) {
        CourseVH holder = getCacheVH(position);
        int type = getItem(position).getType();
        switch (type) {
            case CourseType.audio:
            case CourseType.pic_audio: {
                showView(holder.getLayoutMedia());
                holder.getTvMedia().setText(getItem(position).getString(TCourse.time, "音频"));
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
