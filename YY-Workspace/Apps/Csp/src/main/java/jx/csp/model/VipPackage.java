package jx.csp.model;

import jx.csp.model.VipPackage.TPackage;
import lib.ys.model.EVal;

/**
 * @auther HuoXuYu
 * @since 2017/12/11
 */

public class VipPackage extends EVal<TPackage>{

    public enum TPackage {
        id,         //套餐id
        packageCn,  //中文套餐名称
        packageTw,  //繁体套餐名称
        packageUs,  //英文套餐名称

        packageStart,//套餐开始时间
        packageEnd,  //套餐结束时间

        limitTime,  //有效时长 单位：月
        limitMeets, //限制会议数量： 如果该参数为空且是专业版，显示为无限大即不限制会议数量

        usedMeetCount, //已使用会议数量
        hiddenMeetCount, // 套餐过期 隐藏的会议数,
        meetTotalCount, //会议总数
        unlimited,      //是否无期限 0表示否 1表示是

        yearRmb,
        yearUsd,

        monthRmb,
        monthUsd,

        expireDays, // 套餐即将过期天数
        expireRemind, //套餐过期提醒
    }
}
