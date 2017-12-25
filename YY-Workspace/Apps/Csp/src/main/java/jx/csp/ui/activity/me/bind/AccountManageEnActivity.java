package jx.csp.ui.activity.me.bind;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.constant.FormType;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;

/**
 * @auther HuoXuYu
 * @since 2017/11/8
 */

public class AccountManageEnActivity extends BaseAccountActivity {

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_facebook)
                .name(R.string.account_facebook)
                .drawable(R.drawable.form_ic_account_facebook)
                .text(Profile.inst().getBindNickName(BindId.facebook))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_twitter)
                .name(R.string.account_twitter)
                .drawable(R.drawable.form_ic_account_twitter)
                .text(Profile.inst().getBindNickName(BindId.twitter))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_email)
                .name(R.string.account_email)
                .drawable(R.drawable.form_ic_account_email)
                .text(Profile.inst().getString(TProfile.email))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_jingxin)
                .name(R.string.account_jingxin)
                .drawable(R.drawable.form_ic_account_jingxin)
                .text(Profile.inst().getBindNickName(BindId.yaya))
                .hint(R.string.account_not_bind));
    }
}
