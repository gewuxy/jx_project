package yy.doctor.model.meet.module;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;

import lib.network.model.NetworkReq;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.AppEx;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.view.meet.ModuleLayout.EpnType;
import yy.doctor.view.meet.ModuleView;

/**
 * @auther yuansui
 * @since 2017/7/12
 */
abstract public class BaseFunc implements OnNetworkListener {

    private Context mContext;
    private View mLayout;
    private NetworkOpt mNetwork;
    private String mId;

    private MeetDetail mDetail;
    private OnFuncListener mListener;


    public BaseFunc(Context context, MeetDetail detail, OnFuncListener l) {
        mContext = context;
        mDetail = detail;
        mListener = l;

        mLayout = initLayout(context);
        mLayout.setOnClickListener(v -> onClick());
    }

    protected View initLayout(Context context) {
        return new ModuleView(context)
                .setText(getText())
                .setImgResId(getImgResId());
    }

    public View getLayout() {
        return mLayout;
    }

    public void setEnabled(boolean enabled) {
        mLayout.setSelected(enabled);
    }

    public void setId(String id) {
        mId = id;
    }

    abstract protected CharSequence getText();

    @DrawableRes
    abstract protected int getImgResId();

    @ModuleType
    abstract public int getType();

    public void onClick() {
        MeetDetail d = getDetail();

        if (d.getBoolean(TMeetDetail.attention)) {
            // 关注了
            if (d.getBoolean(TMeetDetail.attendAble)) {
                // 可以参加
                int costEpn = d.getInt(TMeetDetail.xsCredits); // 象数
                @EpnType int epnType = d.getInt(TMeetDetail.eduCredits); // 需要还是奖励
                if (!d.getBoolean(TMeetDetail.attended) && costEpn > 0 && needPay(epnType)) {
                    // 没有参加过且需要象数(需要的象数大于0)
                    if (Profile.inst().getInt(TProfile.credits) < costEpn) {
                        // 剩余象数对于需要象数(象数不足)
//                        if (mOnModulesListener != null) {
//                            mOnModulesListener.toShowDialog(DialogType.recharge, v);
//                        }
                    } else {
                        // 象数足够, 支付
//                        if (mOnModulesListener != null) {
//                            mOnModulesListener.toShowDialog(DialogType.pay, v);
//                        }
                    }
                } else {
                    // 参加过(奖励过和支付过), 不需支付(奖励)象数, 直接参加
                    getNetwork().exeNetworkReq(getNetworkReq());
                    if (mListener != null) {
                        mListener.onFuncLoading(getType(), getModuleId());
                    }
                }
            } else {
                // 不能参加的原因
                AppEx.showToast(d.getString(TMeetDetail.reason));
            }
        } else {
            // 提示关注
//            if (mOnModulesListener != null) {
//                mOnModulesListener.toShowDialog(DialogType.attention, v);
//            }
        }
    }

    private boolean needPay(@EpnType int type) {
        if (type == EpnType.need) {
            return true;
        } else {
            return false;
        }
    }

    abstract protected NetworkReq getNetworkReq();

    protected Context getContext() {
        return mContext;
    }

    protected String getModuleId() {
        return mId;
    }

    protected NetworkOpt getNetwork() {
        if (mNetwork == null) {
            mNetwork = new NetworkOpt(this, this);
        }
        return mNetwork;
    }

    protected MeetDetail getDetail() {
        return mDetail;
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        if (mListener != null) {
            mListener.onFuncNormal(getType(), getModuleId());
        }
    }

    protected OnFuncListener getListener() {
        return mListener;
    }

    public interface OnFuncListener {
        void onFuncLoading(int type, String moduleId);

        void onFuncNormal(int type, String moduleId);
    }
}
