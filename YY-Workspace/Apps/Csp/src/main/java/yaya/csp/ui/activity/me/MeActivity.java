package yaya.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yaya.csp.R;
import yaya.csp.model.Profile;
import yaya.csp.model.Profile.TProfile;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;
import yaya.csp.ui.activity.me.profile.ProfileActivity;
import yaya.csp.util.Util;

/**
 * 个人中心
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */

public class MeActivity extends BaseFormActivity {

    private NetworkImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvEmail;

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
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_history)
                .name(R.string.history)
                .related(RelatedId.history));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_flow)
                .name(R.string.flow_manage)
                .related(RelatedId.flow));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_account)
                .name(R.string.account_manage)
                .related(RelatedId.account));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_setting)
                .name(R.string.setting)
                .related(RelatedId.setting));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_help_and_feedback)
                .name(R.string.help_and_feedback)
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
        mTvEmail = findView(R.id.me_header_tv_email);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvName.setText(Profile.inst().getString(TProfile.user_name));
        setOnClickListener(R.id.layout_me_header);
        mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                .renderer(new CircleRenderer())
                .url(Profile.inst().getString(TProfile.avatar))
                .load();
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

            }
            break;
            case RelatedId.flow: {

            }
            break;
            case RelatedId.account: {

            }
            break;
            case RelatedId.setting: {
                startActivity(SettingsActivity.class);
            }
            break;
            case RelatedId.help_and_feedback: {

            }
            break;
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        //修改个人资料
        if (type == NotifyType.profile_change) {
            mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                    .renderer(new CircleRenderer())
                    .url(Profile.inst().getString(TProfile.avatar))
                    .load();

            mTvName.setText(Profile.inst().getString(TProfile.user_name));
            mTvEmail.setText(Profile.inst().getString(TProfile.email));
        }
    }
}
