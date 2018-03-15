package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.AccountManageActivity;
import jx.csp.ui.activity.me.bind.AccountManageEnActivity;
import jx.csp.ui.activity.me.contribute.ContributeHistoryActivity;
import jx.csp.ui.activity.me.profile.ProfileActivity;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseFormActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.shape.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * 个人中心
 *
 * @auther HuoXuYu
 * @since 2017/9/21
 */

public class MeActivity extends BaseFormActivity {

    private NetworkImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvUserName;

    @IntDef({
            RelatedId.history,
            RelatedId.flow,
            RelatedId.account,
            RelatedId.setting,
            RelatedId.help_and_feedback,
            RelatedId.vip,
            RelatedId.guide,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int history = 0;
        int flow = 1;
        int account = 2;
        int setting = 3;
        int help_and_feedback = 4;
        int vip = 5;
        int guide = 6;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .drawable(R.drawable.form_ic_history)
                .layout(R.layout.form_text_me)
                .name(R.string.person_center_history)
                .related(RelatedId.history));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .drawable(R.drawable.form_ic_flow)
                .layout(R.layout.form_text_me)
                .name(R.string.person_center_flow_manage)
                .related(RelatedId.flow));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .drawable(R.drawable.form_ic_account)
                .layout(R.layout.form_text_me)
                .name(R.string.person_center_account_manage)
                .related(RelatedId.account));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .drawable(R.drawable.form_ic_vip_manage)
                .layout(R.layout.form_text_me)
                .name(R.string.person_center_vip_manage)
                .related(RelatedId.vip));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .drawable(R.drawable.form_ic_setting)
                .layout(R.layout.form_text_me)
                .name(R.string.setting)
                .related(RelatedId.setting));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .drawable(R.drawable.form_ic_guide)
                .layout(R.layout.form_text_me)
                .name(R.string.person_center_guide)
                .related(RelatedId.guide));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .drawable(R.drawable.form_ic_help_and_feedback)
                .layout(R.layout.form_text_me)
                .name(R.string.person_center_help_and_feedback)
                .related(RelatedId.help_and_feedback));
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.person_center, this);
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_me_header);
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvAvatar = findView(R.id.me_header_iv);
        mTvName = findView(R.id.me_header_tv_name);
        mTvUserName = findView(R.id.me_header_tv_email);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnClickListener(R.id.layout_me_header);

        //更新用户数据
        exeNetworkReq(UserAPI.uploadProfileInfo().build());

        changeUserName();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_me_header: {
                startActivity(ProfileActivity.class);
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.history: {
                startActivity(ContributeHistoryActivity.class);
            }
            break;
            case RelatedId.flow: {
                startActivity(LiveFlowActivity.class);
            }
            break;
            case RelatedId.account: {
                if (Util.checkAppCn()) {
                    startActivity(AccountManageActivity.class);
                } else {
                    startActivity(AccountManageEnActivity.class);
                }
            }
            break;
            case RelatedId.vip: {
                startActivity(VipManageActivity.class);
            }
            break;
            case RelatedId.setting: {
                startActivity(SettingsActivity.class);
            }
            break;
            case RelatedId.help_and_feedback: {
                startActivity(HelpAndFeedbackActivity.class);
            }
            break;
            case RelatedId.guide: {
                startActivity(ActionActivity.class);
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            Profile.inst().clear();
            YSLog.d(TAG, "个人数据更新成功");
            Profile.inst().update((Profile) r.getData());
            notify(NotifyType.profile_change);
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        //修改个人资料
        if (type == NotifyType.profile_change) {
            changeUserName();
        } else if (type == NotifyType.logout) {
            finish();
        }
    }

    private void changeUserName() {
        mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                .renderer(new CircleRenderer())
                .url(Profile.inst().getString(TProfile.avatar))
                .load();
        mTvName.setText(Profile.inst().getString(TProfile.nickName));

        if (TextUtil.isNotEmpty(Profile.inst().getString(TProfile.mobile))) {
            mTvUserName.setText(Profile.inst().getString(TProfile.mobile));
            //有手机号才展示，不然隐藏，令昵称居中显示
            showView(mTvUserName);
        } else {
            mTvUserName.setText(Profile.inst().getString(TProfile.email));
            showView(mTvUserName);
        }
    }
}
