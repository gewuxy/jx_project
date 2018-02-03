package jx.csp.adapter.me;

import jx.csp.R;
import jx.csp.adapter.VH.me.JoinGreenHandsVH;
import jx.csp.model.me.Action;
import jx.csp.model.me.Action.TAction;
import jx.csp.util.Util;
import lib.ys.adapter.AdapterEx;

/**
 * @author CaiXiang
 * @since 2018/2/3
 */

public class ActionAdapter extends AdapterEx<Action, JoinGreenHandsVH> {

//    @Override
//    protected void refreshView(int position, JoinGreenHandsVH holder, int itemType) {
//        switch (itemType) {
//            case JoinGreenHandsType.meeting: {
//                showView(holder.getDividerTop());
//                goneView(holder.getIvLive());
//                goneView(holder.getIvPpt());
//                int time = 0;
//                Action item = (Action) getItem(position);
//                Course course = item.get(TAction.course);
//                ArrayList<CourseDetail> details = course.getList(TCourse.details);
//                for (int i = 0; i < details.size(); i++) {
//                    time = details.get(i).getInt(TCourseDetail.duration);
//                }
//                holder.getTvTime().setText(Util.getSpecialTimeFormat(time, "'", "''"));
//                holder.getTvTitle().setText(course.getString(TCourse.title));
//                holder.getIvHead().placeHolder(R.drawable.ic_default_record)
//                        .url(details.get(0).getString(TCourseDetail.imgUrl))
//                        .load();
//            }
//            break;
//        }
//    }
//
//    @Override
//    public int getConvertViewResId(int itemType) {
//        if (itemType == JoinGreenHandsType.meeting) {
//            return R.layout.layout_main_meet_card_item;
//        } else {
//            return 0;
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return getItem(position).getJoinGreenHandsType();
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return JoinGreenHandsType.class.getDeclaredFields().length;
//    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_meet_card_item;
    }

    @Override
    protected void refreshView(int position, JoinGreenHandsVH holder) {
        showView(holder.getDividerTop());
        goneView(holder.getIvLive());
        goneView(holder.getIvPpt());
        Action item = getItem(position);
        holder.getTvTime().setText(Util.getSpecialTimeFormat(item.getInt(TAction.duration), "'", "''"));
        holder.getTvTitle().setText(item.getString(TAction.title));
        holder.getIvHead().placeHolder(R.drawable.ic_default_record)
                .url(item.getString(TAction.coverUrl))
                .load();
    }
}
