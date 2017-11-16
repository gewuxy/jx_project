package jx.csp.model;

import java.util.List;
import java.util.Observable;

import jx.csp.constant.BindId;
import jx.csp.model.BindInfo.TBindInfo;
import jx.csp.model.Profile.TProfile;
import jx.csp.sp.SpUser;
import lib.ys.ConstantsEx;
import lib.ys.impl.SingletonImpl;
import lib.ys.model.EVal;
import lib.ys.ui.interfaces.ISingleton;
import lib.ys.util.TextUtil;

/**
 * @author CaiXiang
 * @since 2017/4/1
 */
public class Profile extends EVal<TProfile> implements ISingleton {

    public enum TProfile {
        token,      // 会话令牌
        id,
        avatar,     //头像
        userName,   //姓名
        gender,     //性别
        info,       //个人简介
        email,      //邮箱
        mobile,     //手机
        nickName,   //昵称
        uid,        // 用户id
        country,    //国家
        province,   // 省份
        city,       // 城市
        wx,         //微信
        sina,       //新浪
        facebook,

        @Init(asInt = 0)
        flux,       //流量 保存单位是M 显示单位是G

        @Bind(asList = BindInfo.class)
        bindInfoList,
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

    public String getBindNickName(@BindId int id) {
        List<BindInfo> list = getList(TProfile.bindInfoList);
        if (list != null) {
            for (BindInfo info : list) {
                if (info.getInt(TBindInfo.thirdPartyId) == id) {
                    return info.getString(TBindInfo.nickName);
                }
            }
        }
        return ConstantsEx.KEmpty;
    }

    public String getUserName() {
        String name = TextUtil.isNotEmpty(getString(TProfile.nickName)) ? getString(TProfile.nickName) : getString(TProfile.userName);
        if (TextUtil.isEmpty(name)) {
            name = "noUserName";
        }
        return name;
    }

    public String getUserId() {
        String userId = TextUtil.isNotEmpty(getString(TProfile.uid)) ? getString(TProfile.uid) : getString(TProfile.id);
        if (TextUtil.isEmpty(userId)) {
            userId = String.valueOf(System.currentTimeMillis());
        }
        return userId;
    }
}
