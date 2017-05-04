package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.sp.SpUser;

/**
 * @author CaiXiang
 * @since 2017/4/1
 */
public class Profile extends EVal<TProfile> {

    public enum TProfile {
        token, // 会话令牌
        id,
        username,
        nickname,
    }

    private static Profile mInst = null;

    public synchronized static Profile inst() {
        if (mInst == null) {
            mInst = SpUser.inst().getEV(Profile.class);
            if (mInst == null) {
                mInst = new Profile();
            }
        }
        return mInst;
    }

    /**
     * 是否已登录
     *
     * @return
     */
    public boolean isLogin() {
        return !getString(TProfile.token).isEmpty();
    }
}
