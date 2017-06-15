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
        long duration = getItem(position).getLong(TDetail.duration);
        if (duration > 0) {
            holder.getTvSecondary().setText(getContext().getString(R.string.video_studied) + format(duration));
        }
    }

    public static String format(long time) {
        StringBuffer sb = new StringBuffer();
        long hour = TimeUnit.HOURS.toSeconds(1);
        if (time > hour) {
           sb.append(time / hour).append("时");
            time %= hour;
        }

        long minute = TimeUnit.MINUTES.toSeconds(1);
        if (time > minute) {
           sb.append(time / minute).append("分");
            time %= minute;
        }

        if (time > 0) {
           sb.append(time).append("秒");
        }
        return sb.toString();
    }
}
