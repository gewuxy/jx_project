package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Province.TProvince;
import yy.doctor.sp.SpUser;

/**
 * @author CaiXiang
 * @since 2017/5/5
 */
public class Province extends EVal<TProvince> {

    public enum TProvince{
        alpha,
        id,
        name,
        preId,
        spell,
    }

    private static Province mInst = null;

    public synchronized static Province inst() {
        if (mInst == null) {
            mInst = SpUser.inst().getEV(Province.class);
            if (mInst == null) {
                mInst = new Province();
            }
        }
        return mInst;
    }

}
