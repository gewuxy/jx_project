package jx.csp.adapter.me;

import jx.csp.R;
import jx.csp.adapter.VH.me.HistoryDetailVH;
import jx.csp.model.me.HistoryDetail;
import jx.csp.model.me.HistoryDetail.THistoryDetail;
import lib.ys.adapter.AdapterEx;

/**
 * @auther WangLan
 * @since 2017/10/16
 */

public class HistoryDetailAdapter extends AdapterEx<HistoryDetail,HistoryDetailVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_contribute_history_detail_item;
    }

    @Override
    protected void refreshView(int position, HistoryDetailVH holder) {
        //总页数
        HistoryDetail item = getItem(position);
        String s = String.format(item.getString(THistoryDetail.pageCount)+"%s","页");

        //录播的总时长
        String duration = item.getString(THistoryDetail.duration);
        String timeStr = duration +"''";
        long seconds = Long.parseLong(duration);

        holder.getIvHead().url(item.getString(THistoryDetail.coverUrl)).load();
        holder.getTvTitle().setText(item.getString(THistoryDetail.title));
        holder.getTvPaper().setText(s);
        if (item.getString(THistoryDetail.playType) == "0") {
            holder.getTvState().setText("录播");

            if (seconds > 60){
                long second = seconds % 60;
                long min = seconds / 60;
                timeStr = min + "'" +second+"''";
            }
            holder.getTvTime().setText(timeStr);
        }else{
            holder.getTvState().setText("直播");

            //直播的开始时间转换
          /*  Date d = new Date(Long.parseLong(item.getString(THistoryDetail.startTime)));
            SimpleDateFormat data = new SimpleDateFormat("MM月dd日 HH:mm");
            holder.getTvTime().setText(data.format(d));*/
        }
    }
}