package yy.doctor.activity.me;

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
public class MyCollectionActivity extends BaseFormActivity {

    @IntDef({
            RelatedId.post,
            RelatedId.drug_catalogue,
            RelatedId.physician_proposal,
            RelatedId.physicist_proposal,
    })
    private @interface RelatedId {
        int post = 0;
        int drug_catalogue = 1;
        int physician_proposal = 2;
        int physicist_proposal = 3;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_clinical_guide)
                .related(RelatedId.post)
                .name("帖子")
                .text("2")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_drug_catalogue)
                .related(RelatedId.drug_catalogue)
                .name("药品说明书")
                .text("6")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_physician_proposal)
                .related(RelatedId.physician_proposal)
                .name("医师建议")
                .text("8")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_pharmacist_proposal)
                .related(RelatedId.physicist_proposal)
                .name("药师建议")
                .text("0")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.post: {
                showToast("帖子");
            }
            break;
            case RelatedId.drug_catalogue: {
                showToast("药品说明书");
            }
            break;
            case RelatedId.physician_proposal: {
                showToast("医师建议");
            }
            break;
            case RelatedId.physicist_proposal: {
                showToast("药师建议");
            }
            break;
        }

    }

}
