package yy.doctor.model.home;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import yy.doctor.model.home.RecUnitNum.TRecUnitNum;

/**
 * Created by CaiXiang on 2017/5/15.
 */

public class RecUnitNum extends EVal<TRecUnitNum> implements IHome {

    @Override
    public int getHomeType() {
        return HomeType.unit_num;
    }

    public enum TRecUnitNum {

        /**
         * {@link Attention}
         */
        attention, // 1 表示已经关注    0 表示未关注
        id,    //单位号Id
        nickname,    //单位号昵称
        headimg,    //单位号头像
    }

    @IntDef({
            Attention.no,
            Attention.yes
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Attention {
        int yes = 1; // 已关注
        int no = 0; // 未关注
    }
}
