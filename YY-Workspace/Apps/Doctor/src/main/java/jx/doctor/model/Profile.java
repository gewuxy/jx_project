package jx.doctor.model;

import java.util.Observable;

import lib.ys.impl.SingletonImpl;
import lib.ys.model.EVal;
import lib.ys.ui.interfaces.ISingleton;
import jx.doctor.model.Place.TPlace;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.hospital.HospitalLevel;
import jx.doctor.sp.SpUser;

/**
 * @author CaiXiang
 * @since 2017/4/1
 */
public class Profile extends EVal<TProfile> implements ISingleton {

    public enum TProfile {
        token,      // 会话令牌
        openid,
        id,
        username,   //登录用户名(默认是邮箱)
        nickname,   //昵称
        linkman,    //真实姓名

        integrity,  //完整度
        headimg,    //头像
        hospital,   //医院
        hosLevel,   // 医院等级
        department, //科室
        province,   //省
        city,       //市
        zone,       //区

        category,   //专科一级
        name,       //专科二级
        cmeId,      // CME卡号
        licence,    //执业许可证号
        title,      //职称
        major,      //专长

        address,    //地址
        credits,    //象数

        mobile,
        place,      //职务

        wxNickname, // 昵称WX

        @Bind(value = HospitalLevel.class)
        systemProperties,
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

    public Place getPlace() {
        return new Place(getString(TProfile.province), getString(TProfile.city), getString(TProfile.zone));
    }

    public Profile put(Place place) {
        if (place == null) {
            return this;
        }

        put(TProfile.province, place.getString(TPlace.province));
        put(TProfile.city, place.getString(TPlace.city));
        put(TProfile.zone, place.getString(TPlace.district));

        return this;
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
