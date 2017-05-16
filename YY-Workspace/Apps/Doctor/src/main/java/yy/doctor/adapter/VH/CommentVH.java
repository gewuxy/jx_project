package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/5/2
 */

public class CommentVH extends ViewHolderEx {
    public CommentVH(@NonNull View convertView) {
        super(convertView);
    }

    /**
     * 评论用户的头像的
     *
     * @return
     */
    public NetworkImageView getIcon() {
        return getView(R.id.meeting_comment_iv_icon);
    }

    /**
     * 名字的文本
     *
     * @return
     */
    public TextView getName() {
        return getView(R.id.meeting_comment_tv_name);
    }

    /**
     * 日期的文本
     *
     * @return
     */
    public TextView getDate() {
        return getView(R.id.meeting_comment_tv_date);
    }

    /**
     * 评论内容的文本
     *
     * @return
     */
    public TextView getContent() {
        return getView(R.id.meeting_comment_tv_content);
    }
}
