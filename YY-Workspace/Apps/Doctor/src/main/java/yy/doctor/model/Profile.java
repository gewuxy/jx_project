package yy.doctor.model;

import java.util.Observable;

import lib.ys.impl.SingletonImpl;
import lib.ys.model.EVal;
import lib.ys.ui.interfaces.ISingleton;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.sp.SpUser;

/**
 * @author CaiXiang
 * @since 2017/4/1
 */
public class Profile extends EVal<TProfile> implements ISingleton {

    public enum TProfile {
        token, // 会话令牌
        id,
        username,  //登录用户名
        nickname,  //昵称
        linkman,   //真实姓名
        headimg,
        mobile,
        province,
        city,
        zone,
        credits, //象数
        licence, //执业许可证号
        major,
        place,  //职务
        title,  //职称
        hospital,
        department,  //科室
        address,
        hosLevel, //医院等级
        cmeId,  // CME卡号
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

    @Override
    public <T extends EVal<TProfile>> void update(T source) {
        super.update(source);
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
