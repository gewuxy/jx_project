package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Register.TRegister;
import yy.doctor.sp.SpUser;

/**
 * @author : GuoXuan
 * @sin17/5/4
 */

public class Register extends EVal<TRegister> {
    public enum TRegister {

    }

    private static Register mInst = null;

    public synchronized static Register inst() {
        if (mInst == null) {
            mInst = SpUser.inst().getEV(Register.class);
            if (mInst == null) {
                mInst = new Register();
            }
        }
        return mInst;
    }
}
