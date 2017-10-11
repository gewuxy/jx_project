package yy.doctor.adapter.meeting;

import android.widget.ImageView;
import android.widget.TextView;

import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.CourseVH;
import yy.doctor.model.meet.Course.CourseType;

/**
 * 录播横屏底部Recycler的Adapter
 *
 * @auther : GuoXuan
 * @since : 2017/9/27
 */
public class MeetingRepLAdapter extends MeetingRepPAdapter {

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_ppt_breviary_item_l;
    }

    public void setView(int position) {
        CourseVH holder = getCacheVH(position);
        showView(holder.getLayoutMedia()); // 区别于竖屏有高亮

        int type = getItem(position).getType();
        TextView tv = holder.getTvMedia();
        ImageView iv = holder.getIvMedia();
        switch (type) {
            case CourseType.audio:
            case CourseType.pic_audio: {
                showView(tv);
                showView(iv);
                tv.setText("音频");
                iv.setImageResource(R.drawable.animation_audio);
            }
            break;
            case CourseType.pic: {
                goneView(tv);
                goneView(iv);
            }
            break;
            case CourseType.video: {
                showView(tv);
                showView(iv);
                tv.setText("视频");
                iv.setImageResource(R.drawable.breviary_ic_video);
            }
            break;
        }

    }

}
