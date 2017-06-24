package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.form.FormEx.TFormElem;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * 帮助和反馈
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class HelpAndFeedbackActivity extends BaseFormActivity {

    //更新日志
    private String mUrlUpdateLog = "http://www.medcn.com:8081/v7/view/article/17061509463772693761";
    //免责声明  服务协议
    private String mUrlDisclaimer = "http://www.medcn.com:8081/v7/view/article/17051509491821468946";
    //征稿启事
    private String mUrlContributionInvited = "http://www.medcn.com:8081/v7/view/article/17061510041163617023";
    //敬信科技
    private String mUrlJX = "http://www.medcn.com:8081/v7/view/article/17061510101320742806";

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

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.update_log)
                .name(R.string.update_log)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.content_text)
                .related(RelatedId.disclaimer)
                .name(R.string.disclaimer)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.content_text)
                .related(RelatedId.contribution_invited)
                .name(R.string.contribution_invited)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.content_text)
                .related(RelatedId.jing_xin)
                .name(R.string.jing_xin)
                .build());

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.content_text)
                .related(RelatedId.feedback)
                .name(R.string.opinion_feedback)
                .build());

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

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.update_log: {
                CommonWebViewActivity.nav(this, getString(R.string.update_log), mUrlUpdateLog);
            }
            break;
            case RelatedId.disclaimer: {
                CommonWebViewActivity.nav(this, getString(R.string.disclaimer), mUrlDisclaimer);
            }
            break;
            case RelatedId.contribution_invited: {
                CommonWebViewActivity.nav(this, getString(R.string.contribution_invited), mUrlContributionInvited);
            }
            break;
            case RelatedId.jing_xin: {
                CommonWebViewActivity.nav(this, getString(R.string.jing_xin), mUrlJX);
            }
            break;
            case RelatedId.feedback: {
                try {
                    Intent data = new Intent(Intent.ACTION_SEND);
                    data.setType("plain/text");
                    data.putExtra(Intent.EXTRA_EMAIL, new String[]{"mailto:app@medcn.cn"});
                    data.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
                    data.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(data);
                } catch (Exception e) {
                    showToast(R.string.can_not_find_relevant_software);
                }
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.help_and_feedback_footer_tv_agreement: {
                CommonWebViewActivity.nav(this, getString(R.string.service_agreement), mUrlDisclaimer);
            }
            break;
        }
    }

}
