package yy.doctor.model.notice;

import yy.doctor.sp.SpUser;

/**
 * 通知
 *
 * @author CaiXiang
 * @since 2017/6/14
 */

public class NoticeNum {

    private static NoticeNum mInst = null;
    private static int count;

    public synchronized static NoticeNum inst() {
        if (mInst == null) {
            mInst = new NoticeNum();
            if (SpUser.inst().badgeNum() > 0) {
                count = SpUser.inst().badgeNum();
            } else {
                count = 0;
            }
        }
        return mInst;
    }

    public void add() {
        count++;
        SpUser.inst().updateBadgeNum(count);
    }

    public void reduce() {
        count--;
        if (count < 0) {
            count = 0;
        }
        SpUser.inst().updateBadgeNum(count);
    }

    public int getCount() {
        return SpUser.inst().badgeNum();
    }

}
