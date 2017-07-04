package yy.doctor.adapter.meeting;

import android.widget.TextView;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.VideoVH;
import yy.doctor.model.meet.video.Detail;
import yy.doctor.util.Time;

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
        holder.getTvName().setText(getItem(position).getString(Detail.TDetail.name));
        TextView tvStudyTime = holder.getTvStudy();
        if (getItem(position).getBoolean(Detail.TDetail.type)) {
            // 1是文件
            long duration = getItem(position).getLong(Detail.TDetail.userdtime);
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
