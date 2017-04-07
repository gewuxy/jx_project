package yy.doctor.frag;

import android.support.annotation.IntDef;
import android.view.View;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class DiscoverFrag extends BaseFormFrag {

    @IntDef({
        RelatedId.public_number,
        RelatedId.doctor,
        RelatedId.medicine_news,
        RelatedId.circle,
        RelatedId.data_center,
        RelatedId.elephant_city,
    })
    private @interface RelatedId{
        int public_number=0;
        int doctor=1;
        int medicine_news=2;
        int circle=3;
        int data_center=4;
        int elephant_city=5;
    }

    @Override
    public void initTitleBar() {
        Util.addMenuIcon(getTitleBar(), (MainActivity) getActivity());
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.line_gray)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_public_number)
                .related(RelatedId.public_number)
                .name("公众号")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.line_gray)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_doctor)
                .related(RelatedId.doctor)
                .name("医者")
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_medicine_news)
                .related(RelatedId.medicine_news)
                .name("医药新闻")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.line_gray)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_circle)
                .related(RelatedId.circle)
                .name("圈子")
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_data_center)
                .related(RelatedId.data_center)
                .name("数据中心")
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_elephant_city)
                .related(RelatedId.elephant_city)
                .name("象城")
                .build());

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId=getItem(position).getInt(TFormElem.related);
        switch (relatedId){
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
            case RelatedId.circle: {
                showToast("圈子");
            }
            break;
            case RelatedId.data_center: {
                showToast("数据中心");
            }
            break;
            case RelatedId.elephant_city: {
                showToast("象城");
            }
            break;
        }
    }
}
