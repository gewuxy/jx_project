package yy.doctor.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.action.IntentAction;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.UrlUtil;
import yy.doctor.util.Util;

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

    private TextView mTvVersion;
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

        mVersion = DeviceUtil.getAppVersionName();
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvVersion = findView(R.id.help_and_feedback_header_tv);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvVersion.setText(mVersion);
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
