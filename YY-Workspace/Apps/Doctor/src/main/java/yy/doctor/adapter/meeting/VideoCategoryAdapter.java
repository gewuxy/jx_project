package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.VideoVH;
import yy.doctor.model.meet.video.Detail;

/**
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class VideoCategoryAdapter extends AdapterEx<Detail, VideoVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_video_category;
    }

    @Override
    protected void refreshView(int position, VideoVH holder) {
        setOnViewClickListener(position, holder.getLayout());
        holder.getTvCategory().setText(getItem(position).getString(Detail.TDetail.name));
    }
}
