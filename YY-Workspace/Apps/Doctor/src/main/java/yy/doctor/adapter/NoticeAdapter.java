package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import yy.doctor.R;
import yy.doctor.adapter.VH.NoticeVH;
import yy.doctor.model.Notice;
import yy.doctor.model.Notice.TNotice;

/**
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
        holder.getTvTime().setText(TimeUtil.formatMilli(item.getLong(TNotice.time), TimeFormat.from_y_to_m_24));
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
