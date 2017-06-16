package yy.doctor.model;

import yy.doctor.sp.SpUser;

/**
 * 通知
 *
 * @author CaiXiang
 * @since 2017/6/14
 */

public class NoticeSize {

    private static NoticeSize mInst = null;
    private static int count;

    public synchronized static NoticeSize inst() {
        if (mInst == null) {
            mInst = new NoticeSize();
            count = 0;
        }
        return mInst;
    }

    public void add() {
        count ++ ;
        SpUser.inst().updateBadgeNum(count);
    }

    public void reduce() {
        count --;
        SpUser.inst().updateBadgeNum(count);
    }

    public int getCount() {
        return SpUser.inst().badgeNum();
    }

}
