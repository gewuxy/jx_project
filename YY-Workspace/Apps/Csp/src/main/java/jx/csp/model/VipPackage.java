package jx.csp.model;

import java.util.Observable;

import jx.csp.model.VipPackage.TPackage;
import jx.csp.sp.SpUser;
import lib.ys.impl.SingletonImpl;
import lib.ys.model.EVal;
import lib.ys.ui.interfaces.ISingleton;

/**
 * @auther Huoxuyu
 * @since 2017/12/11
 */

public class VipPackage extends EVal<TPackage> implements ISingleton {

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
    }

    private static VipPackage mInst = null;

    public synchronized static VipPackage inst() {
        if (mInst == null) {
            mInst = SpUser.inst().getEV(VipPackage.class);
            if (mInst == null) {
                mInst = new VipPackage();
                SingletonImpl.inst().addObserver(mInst);
            }
        }
        return mInst;
    }

    public void update(VipPackage source) {
        put(source);
        saveToSp();
    }

    public void saveToSp() {
        SpUser.inst().save(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        mInst = null;
    }
}
