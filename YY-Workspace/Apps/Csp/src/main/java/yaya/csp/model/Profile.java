package yaya.csp.model;

import java.util.Observable;

import lib.ys.impl.SingletonImpl;
import lib.ys.model.EVal;
import lib.ys.ui.interfaces.ISingleton;
import yaya.csp.model.Profile.TProfile;
import yaya.csp.sp.SpUser;

/**
 * @author CaiXiang
 * @since 2017/4/1
 */
public class Profile extends EVal<TProfile> implements ISingleton {

    public enum TProfile {
        token,      // 会话令牌
        id,
        avatar,     //头像
        user_name,  //姓名
        email,  //邮箱
        info,       //个人简介
    }

    private static Profile mInst = null;

    public synchronized static Profile inst() {
        if (mInst == null) {
            mInst = SpUser.inst().getEV(Profile.class);
            if (mInst == null) {
                mInst = new Profile();
                SingletonImpl.inst().addObserver(mInst);
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

    public void update(Profile source) {
        put(source);
        saveToSp();
    }

    public void saveToSp() {
        SpUser.inst().save(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        mInst = null;
    }

}
