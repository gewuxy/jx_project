package jx.csp.model;

import java.util.Observable;

import lib.ys.impl.SingletonImpl;
import lib.ys.model.EVal;
import lib.ys.ui.interfaces.ISingleton;
import jx.csp.model.Profile.TProfile;
import jx.csp.sp.SpUser;

/**
 * @author CaiXiang
 * @since 2017/4/1
 */
public class Profile extends EVal<TProfile> implements ISingleton {

    public enum TProfile {
        token,      // 会话令牌
        id,
        headimg,     //头像
        userName,  //姓名
        gender,     //性别
        info,       //个人简介
        email,      //邮箱
        phone,      //手机
        wx,         //微信
        sina,       //新浪
        facebook,
        twitter,
        yaya,       //yaya医师
        nickName,    //昵称

        @Init(asInt = 0)
        flowrate,    //流量
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
