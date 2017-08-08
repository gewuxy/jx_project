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
import lib.ys.ConstantsEx;
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
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.me.epn.EpnRechargeActivity;
import yy.doctor.ui.activity.me.profile.ModifyTextActivity;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivity.AttentionUnitNum;
import yy.doctor.view.meet.ModuleView;

import static yy.doctor.model.Profile.TProfile.cmeId;

/**
 * @auther yuansui
 * @since 2017/7/12
 */
abstract public class BaseFunc implements OnNetworkListener, OnClickListener {

    private final int KIdAttention = 1;
    private final int KIdModule = 2;

    private Context mContext;
    private NetworkOpt mNetwork;
    private OnFuncListener mListener;

    private View mLayout; // 单个模块
    private String mId; // 模块id
    private MeetDetail mDetail; // 会议详情信息

    private HintDialogMain mDialogAttention; // 关注
    private HintDialogMain mDialogCme; // 学分
    private HintDialogMain mDialogEpnNotEnough; // 象数不足
    private HintDialogMain mDialogPayEpn; // 支付象数

    public interface OnFuncListener {
        void onFuncLoading(int type, String moduleId);

        void onFuncNormal(int type, String moduleId);

    }

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

    public void setEnabled(boolean enabled) {
        mLayout.setSelected(enabled);
    }

    public View getLayout() {
        return mLayout;
    }

    public void setId(String id) {
        mId = id;
    }

    protected Context getContext() {
        return mContext;
    }

    protected String getModuleId() {
        return mId;
    }

    protected String getMeetId() {
        return getDetail().getString(TMeetDetail.id);
    }

    protected MeetDetail getDetail() {
        return mDetail;
    }

    protected OnFuncListener getListener() {
        return mListener;
    }

    protected NetworkOpt getNetwork() {
        if (mNetwork == null) {
            mNetwork = new NetworkOpt(this, this);
        }
        return mNetwork;
    }

    @Override
    public void onClick(View v) {
        // 是否有模块-> 是否已关注-> 是否能参加-> 是否有学分(-> 是否已填写cme)-> 是否要支付(-> 象数是否充足)

        // 检查存在
        if (!mLayout.isSelected()) {
            return;
        }

        MeetDetail d = getDetail();
        // 检查关注
        if (!d.getBoolean(TMeetDetail.attention)) {
            // 未关注
            attention();
            return;
        }

        // 检查参加
        if (!d.getBoolean(TMeetDetail.attendAble)) {
            // 不能参加, 提示原因
            AppEx.showToast(d.getString(TMeetDetail.reason));
            return;
        }

        // 检查学分
        int credits = getDetail().getInt(TMeetDetail.eduCredits); // 有学分
        boolean reward = getDetail().getBoolean(TMeetDetail.rewardCredit); // 是否奖励
        if (credits > 0 && reward) {
            // 奖励学分
            boolean haveCme = Profile.inst().getString(TProfile.cmeId) == ConstantsEx.KEmptyValue;
            // 检查cme卡号
            if (haveCme) {
                // 没有填cme卡号
                cmeNotFinish();
                return;
            }
        }

        // 检查支付
        boolean attended = d.getBoolean(TMeetDetail.attended); // 是否参加过
        int costEpn = d.getInt(TMeetDetail.xsCredits); // 象数
        boolean epnType = getDetail().getBoolean(TMeetDetail.requiredXs); // 是否需要(奖励)
        if (attended || costEpn == 0 || epnType) {
            // 参加过(奖励过和支付过), 不需支付象数, 奖励
            attend();
            return;
        }

        // 检查象数
        if (Profile.inst().getInt(TProfile.credits) < costEpn) {
            // 剩余象数对于需要象数(象数不足), 提示充值
            epnNotEnough();
        } else {
            // 象数足够, 提示支付
            payEpn();
        }
    }

    /**
     * 诱导关注
     */
    private void attention() {
        mDialogAttention = new HintDialogMain(getContext());
        mDialogAttention.setHint("请先关注会议");
        mDialogAttention.addButton("确认关注", v1 -> {
            mDialogAttention.dismiss();
            App.showToast("关注成功");
            getDetail().put(TMeetDetail.attention, true);
            int id = getDetail().getInt(TMeetDetail.pubUserId);
            Notifier.inst().notify(NotifyType.unit_num_attention_change,
                    new AttentionUnitNum(id, Attention.yes));
            getNetwork().exeNetworkReq(KIdAttention, NetFactory.attention(id, Attention.yes));
        });
        mDialogAttention.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogAttention.dismiss());
        mDialogAttention.show();
    }

    /**
     * 未填写cme卡号
     */
    private void cmeNotFinish() {
        mDialogCme = new HintDialogMain(getContext());
        mDialogCme.setHint("填写CME卡号才能获得学分");
        mDialogCme.addButton("完善资料", R.color.text_666, v -> {
            mDialogCme.dismiss();
            LaunchUtil.startActivity(getContext(), ModifyTextActivity.newIntent(getContext(), getContext().getString(R.string.user_CME_number), cmeId));
        });
        mDialogCme.addButton("取消", R.color.text_666, v -> mDialogCme.dismiss());
        mDialogCme.show();
    }

    /**
     * 象数不足
     */
    private void epnNotEnough() {
        mDialogEpnNotEnough = new HintDialogMain(getContext());
        mDialogEpnNotEnough.setHint("您的剩余象数不足所需象数值, 请充值象数后继续");
        mDialogEpnNotEnough.addButton("充值象数", v1 -> {
            mDialogEpnNotEnough.dismiss();
            LaunchUtil.startActivity(getContext(), EpnRechargeActivity.class);
        });
        mDialogEpnNotEnough.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogEpnNotEnough.dismiss());
        mDialogEpnNotEnough.show();
    }

    /**
     * 提醒支付
     */
    private void payEpn() {
        mDialogPayEpn = new HintDialogMain(getContext());
        mDialogPayEpn.setHint(String.format(getContext().getString(R.string.need_pay), getDetail().getInt(TMeetDetail.xsCredits)));
        mDialogPayEpn.addButton("确认支付", v1 -> {
            mDialogPayEpn.dismiss();
//            Notifier.inst().notify(NotifyType.profile_change,);
            getDetail().put(TMeetDetail.attended, true);
            attend();
        });
        mDialogPayEpn.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogPayEpn.dismiss());
        mDialogPayEpn.show();
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

    @ModuleType
    abstract public int getType();

    @DrawableRes
    abstract protected int getImgResId();

    abstract protected CharSequence getText();

    abstract protected NetworkReq getNetworkReq();

    abstract protected void onNetworkSuccess(Object result);

    abstract protected Object onNetworkResponse(NetworkResp r) throws Exception;

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
        if (mDialogCme != null) {
            mDialogCme.dismiss();
        }
    }
}
