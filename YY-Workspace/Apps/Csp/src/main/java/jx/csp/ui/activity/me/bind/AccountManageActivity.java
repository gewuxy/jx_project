package jx.csp.ui.activity.me.bind;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.constant.FormType;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;

/**
 * 账号管理
 *
 * @auther HuoXuYu
 * @since 2017/9/26
 */
public class AccountManageActivity extends BaseAccountActivity {

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_phone)
                .name(R.string.account_phone)
                .drawable(R.drawable.form_ic_account_phone)
                .text(Profile.inst().getString(TProfile.mobile))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_wx)
                .name(R.string.account_wx)
                .drawable(R.drawable.form_ic_account_wx)
                .text(Profile.inst().getBindNickName(BindId.wechat))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_sina)
                .name(R.string.account_sina)
                .drawable(R.drawable.form_ic_account_sina)
                .text(Profile.inst().getBindNickName(BindId.sina))
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
