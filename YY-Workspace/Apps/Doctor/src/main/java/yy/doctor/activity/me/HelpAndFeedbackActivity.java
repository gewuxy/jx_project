package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import lib.ys.form.FormItemEx.TFormElem;
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

    @IntDef({
            RelatedId.check_version,
            RelatedId.feedback,
            RelatedId.update_log,
            RelatedId.disclaimer,
            RelatedId.notice,
            RelatedId.jing_xin,
    })
    private @interface RelatedId {
        int check_version = 0;
        int feedback = 1;
        int update_log = 2;
        int disclaimer = 3;
        int notice = 4;
        int jing_xin = 5;
    }

    private TextView mTvVersion;
    private String mVersion;

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "帮助与反馈", this);
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_help_and_feedback_header);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.check_version)
                .name("检查版本更新")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.feedback)
                .name("意见反馈")
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.update_log)
                .name("更新日志")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.disclaimer)
                .name("免责声明")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.notice)
                .name("征稿启事")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.jing_xin)
                .name("敬信")
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
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.check_version: {
                showToast("检查版本更新");
            }
            break;
            case RelatedId.feedback: {
                Intent data = new Intent(Intent.ACTION_SEND);
                data.setType("plain/text");
                data.putExtra(Intent.EXTRA_EMAIL, new String[]{"mailto:app@medcn.cn"});
                data.putExtra(Intent.EXTRA_SUBJECT, "YaYa医师--意见反馈");
                data.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(data);
            }
            break;
            case RelatedId.update_log: {
                startActivity(UpdateLogActivity.class);
            }
            break;
            case RelatedId.disclaimer: {
                startActivity(DisclaimerActivity.class);
            }
            break;
            case RelatedId.notice: {
                startActivity(ContributionInvitedActivity.class);
            }
            break;
            case RelatedId.jing_xin: {
                startActivity(JXActivity.class);
            }
            break;
        }
    }

}
