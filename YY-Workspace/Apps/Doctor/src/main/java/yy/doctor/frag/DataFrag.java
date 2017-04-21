package yy.doctor.frag;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.activity.data.DataCenterActivity;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class DataFrag extends BaseFormFrag {

    @IntDef({
            RelatedId.public_number,
            RelatedId.doctor,
            RelatedId.medicine_news,
            RelatedId.data_center,
            RelatedId.elephant_city,
    })
    private @interface RelatedId {
        int public_number = 0;
        int doctor = 1;
        int medicine_news = 2;
        int data_center = 3;
        int elephant_city = 4;
    }

    @Override
    public void initTitleBar() {

        getTitleBar().addTextViewMid("数据中心");
        getTitleBar().addViewRight(R.mipmap.ic_search, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("585");
            }
        });

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_public_number)
                .related(RelatedId.public_number)
                .name("单位号")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_doctor)
                .related(RelatedId.doctor)
                .name("医者")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_medicine_news)
                .related(RelatedId.medicine_news)
                .name("医药新闻")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_data_center)
                .related(RelatedId.data_center)
                .name("数据中心")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_epc)
                .related(RelatedId.elephant_city)
                .name("象城")
                .build());

        addItem(new Builder(FormType.divider).build());

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.public_number: {
                showToast("公众号");
            }
            break;
            case RelatedId.doctor: {
                showToast("医者");
            }
            break;
            case RelatedId.medicine_news: {
                showToast("医药新闻");
            }
            break;
            case RelatedId.data_center: {
                Intent intent = new Intent(getContext(), DataCenterActivity.class);
                startActivity(intent);
            }
            break;
            case RelatedId.elephant_city: {
                showToast("象城");
            }
            break;
        }
    }

}
