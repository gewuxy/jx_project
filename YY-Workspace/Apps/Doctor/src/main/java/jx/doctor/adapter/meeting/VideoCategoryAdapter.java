package jx.doctor.adapter.meeting;

import android.widget.TextView;

import lib.ys.adapter.AdapterEx;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.VideoVH;
import jx.doctor.model.meet.video.Detail;
import jx.doctor.model.meet.video.Detail.TDetail;
import jx.doctor.util.Time;

/**
 * 视频列表的Adapter
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class VideoCategoryAdapter extends AdapterEx<Detail, VideoVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_video_category_item;
    }

    @Override
    protected void refreshView(int position, VideoVH holder) {
        setOnViewClickListener(position, holder.getLayout());
        holder.getTvName().setText(getItem(position).getString(TDetail.name));
        TextView tvStudyTime = holder.getTvStudy();
        if (getItem(position).getBoolean(TDetail.type)) {
            // 1是文件
            long duration = getItem(position).getLong(TDetail.userdtime);
            if (duration > 0) {
                tvStudyTime.setText(getContext().getString(R.string.video_add_up) + Time.secondFormat(duration));
            } else {
                tvStudyTime.setText(R.string.video_no_see);
            }
        } else {
            // 0是文件夹
            goneView(tvStudyTime);
        }
    }

}
