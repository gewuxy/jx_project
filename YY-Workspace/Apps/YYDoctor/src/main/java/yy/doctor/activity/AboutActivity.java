package yy.doctor.activity;

import android.support.annotation.IntDef;
import android.view.View;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/12
 */
public class AboutActivity extends BaseFormActivity {

    @IntDef({
            RelatedId.comment,
            RelatedId.feedback,
            RelatedId.update_log,
            RelatedId.disclaimer,
            RelatedId.notice,
            RelatedId.jingxin,
    })
    private @interface RelatedId {
        int comment = 0;
        int feedback = 1;
        int update_log = 2;
        int disclaimer = 3;
        int notice = 4;
        int jingxin = 5;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.activity_about_head);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img)
                .related(RelatedId.comment)
                .name("去评价")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img)
                .related(RelatedId.feedback)
                .name("意见反馈")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.app_bg)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img)
                .related(RelatedId.update_log)
                .name("更新日志")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img)
                .related(RelatedId.disclaimer)
                .name("免责声明")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img)
                .related(RelatedId.notice)
                .name("征稿启事")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img)
                .related(RelatedId.jingxin)
                .name("敬信")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedid = getItem(position).getInt(TFormElem.related);
        switch (relatedid) {
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
                showToast("免责声明");
            }
            break;
            case RelatedId.notice: {
                showToast("征稿启事");
            }
            break;
            case RelatedId.jingxin: {
                showToast("敬信");
            }
            break;
        }

    }

}
