package yy.doctor.model.meet.ppt;

import lib.ys.model.EVal;
import yy.doctor.model.meet.ppt.LiveOrder.TLiveOrder;

/**
 * 直播指令
 *
 * @auther : GuoXuan
 * @since : 2017/10/18
 */
public class LiveOrder extends EVal<TLiveOrder> {

    public enum TLiveOrder {
        pageNum, // ppt当前页面
        audioUrl, // url
    }

}
