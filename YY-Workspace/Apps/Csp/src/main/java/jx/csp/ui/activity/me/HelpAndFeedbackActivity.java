package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * @auther Huoxuyu
 * @since 2017/10/16
 */

public class HelpAndFeedbackActivity extends BaseFormActivity {

    @IntDef({

    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int update_log = 1;
        int service_agreement = 2;
        int help = 3;
        int opinion_feedback = 4;
        int about_us = 5;
    }

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

        addItem(Form.create(FormType.text)
                .related(RelatedId.update_log)
                .name(R.string.help_and_feedback_update_log));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.service_agreement)
                .name(R.string.help_and_feedback_service_agreement));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.help)
                .name(R.string.help_and_feedback_help));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.opinion_feedback)
                .name(R.string.help_and_feedback_opinion_feedback));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.about_us)
                .name(R.string.help_and_feedback_about_us));
    }

    @Override
    public void setViews() {
        super.setViews();

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.update_log: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_update_log), null).route(this);
            }
            break;
            case RelatedId.service_agreement: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_service_agreement), null).route(this);
            }
            break;
            case RelatedId.help: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_help), null).route(this);
            }
            break;
            case RelatedId.opinion_feedback: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_opinion_feedback), null).route(this);
            }
            break;
            case RelatedId.about_us: {
                CommonWebViewActivityRouter.create(getString(R.string.help_and_feedback_about_us), null).route(this);
            }
            break;
        }
    }
}
