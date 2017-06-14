package yy.doctor.adapter.meeting;

import java.util.concurrent.TimeUnit;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.VideoVH;
import yy.doctor.model.meet.video.Detail;
import yy.doctor.model.meet.video.Detail.TDetail;

/**
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class VideoDetailAdapter extends AdapterEx<Detail, VideoVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_video_detail;
    }

    @Override
    protected void refreshView(int position, VideoVH holder) {
        setOnViewClickListener(position, holder.getLayout());
        holder.getTvMain().setText(getItem(position).getString(TDetail.name));
        holder.getTvSecondary().setText(getContext().getString(R.string.video_studied) + format(getItem(position).getLong(TDetail.duration)));
    }

    private String format(long time) {
        StringBuffer sb = new StringBuffer(time + "");
        if (time > TimeUnit.DAYS.toSeconds(1)) {
            // FIXME: 2017/6/14
        }
        return sb.toString();
    }
}
