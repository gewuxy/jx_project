package jx.csp.ui.activity.me;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.constant.LangType;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.me.bind.AccountManageActivity;
import jx.csp.ui.activity.me.bind.AccountManageEnActivity;
import jx.csp.ui.activity.me.profile.ProfileActivity;
import jx.csp.util.Util;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * 个人中心
 *
 * @auther Huoxuyu
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
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int history = 0;
        int flow = 1;
        int account = 2;
        int setting = 3;
        int help_and_feedback = 4;
    }

    @Override
    public void initData(Bundle state) {
        super.initData(state);

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
                .drawable(R.drawable.form_ic_setting)
                .layout(R.layout.form_text_me)
                .name(R.string.setting)
                .related(RelatedId.setting));

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
                if (SpApp.inst().getLangType() == LangType.en) {
                    startActivity(FlowRateManageEnActivity.class);
                } else {
                    startActivity(FlowRateManageActivity.class);
                }
            }
            break;
            case RelatedId.account: {
                if (SpApp.inst().getLangType() == LangType.en) {
                    startActivity(AccountManageEnActivity.class);
                } else {
                    startActivity(AccountManageActivity.class);
                }
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
        } else {
            mTvUserName.setText(Profile.inst().getString(TProfile.email));
        }
    }
}
