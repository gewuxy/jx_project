package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.CommentHistories.TCommentHistories;

/**
 * @auther : GuoXuan
 * @since : 2017/6/14
 */

public class CommentHistories extends EVal<TCommentHistories> {

    public enum TCommentHistories {
        @Bind(asList = Comment.class)
        datas,
    }
}
