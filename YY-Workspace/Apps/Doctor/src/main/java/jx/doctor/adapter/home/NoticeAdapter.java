package jx.doctor.adapter.home;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;
import jx.doctor.R;
import jx.doctor.adapter.VH.home.NoticeVH;
import jx.doctor.model.notice.Notice;
import jx.doctor.model.notice.Notice.TNotice;

/**
 * 通知的adapter
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class NoticeAdapter extends AdapterEx<Notice, NoticeVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_notice_item;
    }

    @Override
    protected void refreshView(int position, NoticeVH holder) {

        Notice item = getItem(position);
        holder.getTvContent().setText(item.getString(TNotice.content));
        holder.getTvUnitUnm().setText(item.getString(TNotice.from));
        holder.getTvTime().setText(TimeFormatter.milli(item.getLong(TNotice.time), TimeFormat.from_y_to_m_24));
        //判断是否读过
        if (item.getBoolean(TNotice.is_read)) {
            hideView(holder.getIvDot());
        } else {
            showView(holder.getIvDot());
        }
        //判断是否有箭头图标   有图标就有跳转事件
        if (item.getInt(TNotice.msgType) == 1) {
            showView(holder.getIvArrow());
        } else {
            hideView(holder.getIvArrow());
        }
    }

}
