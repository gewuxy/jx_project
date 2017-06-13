package yy.doctor.model.home;

import android.support.annotation.IntDef;

import lib.ys.model.EVal;
import yy.doctor.model.home.RecUnitNum.TRecUnitNum;

/**
 * Created by XPS on 2017/5/15.
 */

public class RecUnitNum extends EVal<TRecUnitNum> implements IHome {

    @Override
    public int getType() {
        return HomeType.unit_num;
    }

    public enum TRecUnitNum {

        /**
         * {@link Attention}
         */
        attention, // 1 表示已经关注    0 表示未关注
        id,    //单位号Id
        nickname,    //公众号昵称
        headimg,    //公众号头像
    }

    @IntDef({
            Attention.no,
            Attention.yes
    })
    public @interface Attention {
        int yes = 1; // 已关注
        int no = 0; // 未关注
    }
}
