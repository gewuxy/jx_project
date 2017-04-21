package yy.doctor.activity.data;

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
public class DataCenterActivity extends BaseFormActivity {

    @IntDef({
            RelatedId.clinical_guide,
            RelatedId.clinical_share,
            RelatedId.drug_catalogue,
            RelatedId.ts_library,
            RelatedId.physician_proposal,
            RelatedId.pharmacist_proposal,

    })
    private @interface RelatedId {
        int clinical_guide = 0;
        int clinical_share = 1;
        int drug_catalogue = 2;
        int ts_library = 3;
        int physician_proposal = 4;
        int pharmacist_proposal = 5;
    }

    @Override
    public void initNavBar() {

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.app_bg)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_clinical_guide)
                .related(RelatedId.clinical_guide)
                .name("临床指南")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_clinical_share)
                .related(RelatedId.clinical_share)
                .name("临床分享")
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

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_drug_catalogue)
                .related(RelatedId.drug_catalogue)
                .name("药品目录")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_ts_library)
                .related(RelatedId.ts_library)
                .name("汤森路透疾病图书馆")
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

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_physician_proposal)
                .related(RelatedId.physician_proposal)
                .name("医师建议")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.data_center_ic_pharmacist_proposal)
                .related(RelatedId.pharmacist_proposal)
                .name("药师建议")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.clinical_guide: {
                showToast("临床指南");
            }
            break;
            case RelatedId.clinical_share: {
                showToast("临床分享");
            }
            break;
            case RelatedId.drug_catalogue: {
                showToast("药品目录");
            }
            break;
            case RelatedId.ts_library: {
                showToast("图书馆");
            }
            break;
            case RelatedId.physician_proposal: {
                showToast("医师建议");
            }
            break;
            case RelatedId.pharmacist_proposal: {
                showToast("药师建议");
            }
            break;
        }


    }

}
