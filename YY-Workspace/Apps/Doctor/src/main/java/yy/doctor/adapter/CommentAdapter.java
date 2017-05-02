package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.CommentVH;

/**
 * 评论的Adapter
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */

public class CommentAdapter extends AdapterEx<String, CommentVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_comment_item;
    }

    @Override
    protected void refreshView(int position, CommentVH holder) {
        holder.getIcon().placeHolder(R.mipmap.meeting_record_ic_symbol).load();
    }
}
