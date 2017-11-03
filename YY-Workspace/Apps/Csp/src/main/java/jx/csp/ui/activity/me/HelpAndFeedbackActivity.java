package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.model.def.FormType;
import jx.csp.model.form.Form;
import jx.csp.network.UrlUtil;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.util.Util;
import lib.ys.action.IntentAction;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PackageUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * 帮助与反馈
 *
 * @auther Huoxuyu
 * @since 2017/10/16
 */

public class HelpAndFeedbackActivity extends BaseFormActivity {

    //更新日志
    private String mUrlUpdateLog = UrlUtil.getBaseUrl() + "/view/17110216044331146598";
    //免责声明  服务协议
    private String mUrlDisclaimer = UrlUtil.getBaseUrl() + "/view/17110215475385132976";
    //帮助
    private String mUrlHelp = UrlUtil.getBaseUrl() + "/view/17110216023876150654";
    //关于我们
    private String mUrlAboutUs = UrlUtil.getBaseUrl() + "/view/17110216051754139182";

    @IntDef({
            RelatedId.update_log,
            RelatedId.service_agreement,
            RelatedId.help,
            RelatedId.opinion_feedback,
            RelatedId.about_us,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int update_log = 1;
        int service_agreement = 2;
        int help = 3;
        int opinion_feedback = 4;
        int about_us = 5;
    }

    private TextView mTvAppName;
    private TextView mTvVersion;
    private String mVersion;

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.person_center_help_and_feedback, this);
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_help_and_feekback_header);
    }

    @Override
    public void initData() {
        super.initData();
        mVersion = PackageUtil.getAppVersionName();

        addItem(Form.create(FormType.text)
                .related(RelatedId.update_log)
                .nameColor(ResLoader.getColor(R.color.text_333))
                .name(R.string.help_and_feedback_update_log));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .related(RelatedId.service_agreement)
                .nameColor(ResLoader.getColor(R.color.text_333))
                .name(R.string.help_and_feedback_service_agreement));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .related(RelatedId.help)
                .nameColor(ResLoader.getColor(R.color.text_333))
                .name(R.string.help_and_feedback_help));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .related(RelatedId.opinion_feedback)
                .nameColor(ResLoader.getColor(R.color.text_333))
                .name(R.string.help_and_feedback_opinion_feedback));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.about_us)
                .nameColor(ResLoader.getColor(R.color.text_333))
                .name(R.string.help_and_feedback_about_us));
    }

    @Override
    public void findViews() {
        super.findViews();
        mTvAppName = findView(R.id.help_and_feedback_tv_app_name);
        mTvVersion = findView(R.id.help_and_feedback_tv_version);
    }

    @Override
    public void setViews() {
        super.setViews();
        mTvAppName.setText(PackageUtil.getMetaValue("APP_NAME"));
        mTvVersion.setText("V" + mVersion);
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);
        // FIXME: 2017/10/26 暂时没有H5
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.update_log: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_update_log), mUrlUpdateLog).route(this);
            }
            break;
            case RelatedId.service_agreement: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_service_agreement), mUrlDisclaimer).route(this);
            }
            break;
            case RelatedId.help: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_help), mUrlHelp).route(this);
            }
            break;
            case RelatedId.opinion_feedback: {
//                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_opinion_feedback), null).route(this);
                IntentAction.mail()
                        .address("app@medcn.cn")
                        .subject(R.string.help_and_feedback_email_subject)
                        .alert("没有邮件类应用")
                        .launch();
            }
            break;
            case RelatedId.about_us: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_about_us), mUrlAboutUs).route(this);
            }
            break;
        }
    }
}
