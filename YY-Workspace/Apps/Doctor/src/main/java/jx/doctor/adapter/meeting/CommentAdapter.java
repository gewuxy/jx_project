package jx.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeFormatter;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.CommentVH;
import jx.doctor.model.meet.Comment;
import jx.doctor.model.meet.Comment.TComment;

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
        holder.getIv()
                .placeHolder(R.drawable.ic_default_meeting_comment)
                .url(getItem(position).getString(TComment.headimg))
                .load();
        holder.getTvName().setText(getItem(position).getString(TComment.sender));
        holder.getTvDate().setText(TimeFormatter.milli(getItem(position).getString(TComment.sendTime), "MM-dd HH:mm"));
        String message = getItem(position).getString(TComment.message);
        if (TextUtil.isNotEmpty(message)) {
            holder.getTvContent().setText(message);
        }
    }
}
