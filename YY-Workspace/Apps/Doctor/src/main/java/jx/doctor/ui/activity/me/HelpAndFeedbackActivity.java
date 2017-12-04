package jx.doctor.ui.activity.me;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.action.IntentAction;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PackageUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.res.ResUtil.ResDefType;
import lib.jx.ui.activity.base.BaseFormActivity;
import jx.doctor.R;
import jx.doctor.model.form.Form;
import jx.doctor.model.form.FormType;
import jx.doctor.network.UrlUtil;
import jx.doctor.ui.activity.CommonWebViewActivityRouter;
import jx.doctor.util.Util;

/**
 * 帮助和反馈
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class HelpAndFeedbackActivity extends BaseFormActivity {

    //更新日志
    private String mUrlUpdateLog = UrlUtil.getHostName() + "/view/article/17061509463772693761";
    //免责声明  服务协议
    private String mUrlDisclaimer = UrlUtil.getHostName() + "/view/article/17051509491821468946";
    //征稿启事
    private String mUrlContributionInvited = UrlUtil.getHostName() + "/view/article/17061510041163617023";
    //敬信科技
    private String mUrlJX = UrlUtil.getHostName() + "/view/article/17061510060938714106";

    @IntDef({
            RelatedId.update_log,
            RelatedId.disclaimer,
            RelatedId.contribution_invited,
            RelatedId.jing_xin,

            RelatedId.feedback,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {

        int update_log = 1;
        int disclaimer = 2;
        int contribution_invited = 3;
        int jing_xin = 4;

        int feedback = 5;
    }

    private TextView mTvAppName;
    private TextView mTvVersion;
    private NetworkImageView mIv;
    private String mVersion;

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.held_and_feedback, this);
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_help_and_feedback_header);
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_help_and_feedback_footer);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.text)
                .related(RelatedId.update_log)
                .name(R.string.update_log));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.disclaimer)
                .name(R.string.disclaimer));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.contribution_invited)
                .name(R.string.contribution_invited));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.jing_xin)
                .name(R.string.jing_xin));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.feedback)
                .name(R.string.opinion_feedback));

        mVersion = PackageUtil.getAppVersionName();
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvAppName = findView(R.id.help_and_feedback_tv_app_name);
        mTvVersion = findView(R.id.help_and_feedback_tv_version);
        mIv = findView(R.id.help_and_feedback_iv);
    }

    @Override
    public void setViews() {
        super.setViews();

        mIv.res(ResLoader.getIdentifier(PackageUtil.getMetaValue("APP_ICON"), ResDefType.mipmap)).load();
        mTvAppName.setText(PackageUtil.getMetaValue("APP_NAME"));
        mTvVersion.setText("V" + mVersion);
        setOnClickListener(R.id.help_and_feedback_footer_tv_agreement);
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.update_log: {
                CommonWebViewActivityRouter.create(getString(R.string.update_log), mUrlUpdateLog)
                        .route(this);
            }
            break;
            case RelatedId.disclaimer: {
                CommonWebViewActivityRouter.create(getString(R.string.disclaimer), mUrlDisclaimer)
                        .route(this);
            }
            break;
            case RelatedId.contribution_invited: {
                CommonWebViewActivityRouter.create(getString(R.string.contribution_invited), mUrlContributionInvited)
                        .route(this);
            }
            break;
            case RelatedId.jing_xin: {
                CommonWebViewActivityRouter.create(getString(R.string.jing_xin), mUrlJX)
                        .route(this);
            }
            break;
            case RelatedId.feedback: {
                IntentAction.mail()
                        .address("app@medcn.cn")
                        .subject(R.string.email_subject)
                        .alert("没有邮件类应用")
                        .launch();
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.help_and_feedback_footer_tv_agreement: {
                CommonWebViewActivityRouter.create(getString(R.string.service_agreement), mUrlDisclaimer)
                        .route(this);
            }
            break;
        }
    }

}
