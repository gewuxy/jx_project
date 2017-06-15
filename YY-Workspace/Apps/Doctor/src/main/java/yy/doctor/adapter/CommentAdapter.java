package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.CommentVH;
import yy.doctor.model.meet.Comment;
import yy.doctor.model.meet.Comment.TComment;

/**
 * 评论的Adapter
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */

public class CommentAdapter extends AdapterEx<Comment, CommentVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_comment_item;
    }

    @Override
    protected void refreshView(int position, CommentVH holder) {
        holder.getIcon().placeHolder(R.mipmap.ic_default_meeting_comment).url(getItem(position).getString(TComment.headimg)).load();
        holder.getName().setText(getItem(position).getString(TComment.sender));
        holder.getDate().setText(TimeUtil.formatMilli(getItem(position).getString(TComment.sendTime), "MM-dd HH:mm"));
        holder.getContent().setText(getItem(position).getString(TComment.message));
    }
}
