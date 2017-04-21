package yy.doctor.activity.me;

import android.support.annotation.IntDef;
import android.view.View;

import lib.ys.form.FormItemEx.TFormElem;
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
            RelatedId.comment,
            RelatedId.feedback,
            RelatedId.update_log,
            RelatedId.disclaimer,
            RelatedId.notice,
            RelatedId.jingxin,
    })
    private @interface RelatedId {
        int check_version = 0;
        int comment = 1;
        int feedback = 2;
        int update_log = 3;
        int disclaimer = 4;
        int notice = 5;
        int jingxin = 6;
    }

    @Override
    public void initNavBar() {

        Util.addBackIcon(getNavBar(), "帮助与反馈", this);

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
                .related(RelatedId.comment)
                .name("去评价")
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
                .related(RelatedId.jingxin)
                .name("敬信")
                .build());

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedid = getItem(position).getInt(TFormElem.related);
        switch (relatedid) {
            case RelatedId.check_version: {
                showToast("检查版本更新");
            }
            break;
            case RelatedId.comment: {
                showToast("去评论");
            }
            break;
            case RelatedId.feedback: {
                showToast("意见反馈");
            }
            break;
            case RelatedId.update_log: {
                showToast("更新日志");
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
            case RelatedId.jingxin: {
                startActivity(JXActivity.class);
            }
            break;
        }

    }

}
