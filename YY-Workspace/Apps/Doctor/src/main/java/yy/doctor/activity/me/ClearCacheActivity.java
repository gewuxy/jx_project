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
 * @since 2017/4/13
 */
public class ClearCacheActivity extends BaseFormActivity {

    @IntDef({
            RelatedId.clear_img_cache,
            RelatedId.clear_sound_cache,
    })
    private @interface RelatedId {
        int clear_img_cache = 0;
        int clear_sound_cache = 1;
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

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.app_bg)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .name("清理图片缓存")
                .related(RelatedId.clear_img_cache)
                .text("88M")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .name("清理声音缓存")
                .related(RelatedId.clear_sound_cache)
                .text("66M")
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
            case RelatedId.clear_img_cache: {
                showToast("88");
            }
            break;
            case RelatedId.clear_sound_cache: {
                showToast("99");
            }
            break;
        }

    }

}
