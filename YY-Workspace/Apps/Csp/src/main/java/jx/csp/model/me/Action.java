package jx.csp.model.me;

import jx.csp.model.me.Action.TAction;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/2/3
 */

public class Action extends EVal<TAction> {

//    @Override
//    public int getJoinGreenHandsType() {
//        return JoinGreenHandsType.meeting;
//    }

    public enum TAction {

        id,  // 课件ID	国内为1 国外为2
        title,  // 课件标题
        type,  // 	0表示新手引导 其他未定义
        pptSize,  // ppt页数
        duration,  // 时间
        coverUrl,
    }

}
