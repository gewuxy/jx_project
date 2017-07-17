package yy.doctor.model.meet.module;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.View.OnClickListener;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.OnNetworkListener;
import lib.ys.AppEx;
import lib.ys.ui.interfaces.impl.NetworkOpt;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.App;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.home.RecUnitNum.Attention;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.EpnType;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.me.epn.EpnRechargeActivity;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivity.AttentionUnitNum;
import yy.doctor.view.meet.ModuleView;

/**
 * @auther yuansui
 * @since 2017/7/12
 */
abstract public class BaseFunc implements OnNetworkListener, OnClickListener {

    private final int KIdAttention = 1;
    private final int KIdModule = 2;

    private Context mContext;
    private View mLayout; // 单个模块
    private NetworkOpt mNetwork;

    private String mId;
    private HintDialogMain mDialogPayEpn; // 支付象数
    private HintDialogMain mDialogEpnNotEnough; // 象数不足

    private HintDialogMain mDialogAttention; // 关注
    private MeetDetail mDetail;
    private OnFuncListener mListener;

    public BaseFunc(Context context, MeetDetail detail, OnFuncListener l) {
        mContext = context;
        mDetail = detail;
        mListener = l;

        mLayout = initLayout(context);
        mLayout.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (mLayout.isSelected()) {
            // 模块存在
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
                            // 剩余象数对于需要象数(象数不足), 提示充值
                            mDialogEpnNotEnough = new HintDialogMain(mContext);
                            mDialogEpnNotEnough.setHint("您的剩余象数不足所需象数值, 请充值象数后继续");
                            mDialogEpnNotEnough.addButton("充值象数", v1 -> {
                                mDialogEpnNotEnough.dismiss();
                                LaunchUtil.startActivity(mContext, EpnRechargeActivity.class);
                            });
                            mDialogEpnNotEnough.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogEpnNotEnough.dismiss());
                            mDialogEpnNotEnough.show();
                        } else {
                            // 象数足够, 提示支付
                            mDialogPayEpn = new HintDialogMain(mContext);
                            mDialogPayEpn.setHint(String.format(getContext().getString(R.string.need_pay), costEpn));
                            mDialogPayEpn.addButton("确认支付", v1 -> {
                                mDialogPayEpn.dismiss();
                                mDetail.put(TMeetDetail.attended, true);
                                attend();
                            });
                            mDialogPayEpn.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogPayEpn.dismiss());
                            mDialogPayEpn.show();
                        }
                    } else {
                        // 参加过(奖励过和支付过), 不需支付(奖励)象数, 直接参加
                        attend();
                    }
                } else {
                    // 不能参加的原因
                    AppEx.showToast(d.getString(TMeetDetail.reason));
                }
            } else {
                // 提示关注
                mDialogAttention = new HintDialogMain(mContext);
                mDialogAttention.setHint("请先关注会议");
                mDialogAttention.addButton("确认关注", v1 -> {
                    mDialogAttention.dismiss();
                    App.showToast("关注成功");
                    mDetail.put(TMeetDetail.attention, true);
                    Notifier.inst().notify(NotifyType.unit_num_attention_change,
                            new AttentionUnitNum(mDetail.getInt(TMeetDetail.pubUserId), Attention.yes));
                    getNetwork().exeNetworkReq(KIdAttention, NetFactory.attention(mDetail.getInt(TMeetDetail.pubUserId), Attention.yes));
                });
                mDialogAttention.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogAttention.dismiss());
                mDialogAttention.show();
            }
        }
    }

    /**
     * 参与会议
     */
    protected void attend() {
        getNetwork().exeNetworkReq(KIdModule, getNetworkReq());
        if (mListener != null) {
            mListener.onFuncLoading(getType(), getModuleId());
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

    abstract protected void onNetworkSuccess(Object result);

    abstract protected  Object onNetworkResponse(NetworkResp r) throws Exception;

    protected Context getContext() {
        return mContext;
    }

    protected String getModuleId() {
        return mId;
    }

    protected String getMeetId() {
        return mDetail.getString(TMeetDetail.id);
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
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdModule) {
            return onNetworkResponse(r);
        }
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (getListener() != null) {
            getListener().onFuncNormal(getType(), getModuleId());
        }
        if (id == KIdModule) {
            onNetworkSuccess(result);
        }
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

    public void onDestroy() {
        if (mDialogPayEpn != null) {
            mDialogPayEpn.dismiss();
        }
        if (mDialogAttention != null) {
            mDialogAttention.dismiss();
        }
        if (mDialogEpnNotEnough != null) {
            mDialogEpnNotEnough.dismiss();
        }
    }
}
