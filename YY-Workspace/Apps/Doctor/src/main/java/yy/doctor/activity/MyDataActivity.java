package yy.doctor.activity;

import android.support.annotation.IntDef;
import android.view.View;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/13
 */
public class MyDataActivity extends BaseFormActivity {

    @IntDef({
            RelatedId.my_elephant,
            RelatedId.my_collection,
            RelatedId.set,
            RelatedId.about,
    })
    private @interface RelatedId {
        int my_elephant = 0;
        int my_collection = 1;
        int set = 2;
        int about = 3;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.activity_my_data_head);
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void findViews() {
        super.findViews();

    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onFormItemClick(View v, int position) {

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);

    }


}
