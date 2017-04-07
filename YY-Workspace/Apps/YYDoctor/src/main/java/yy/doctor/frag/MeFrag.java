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
public class MeFrag extends BaseFormFrag {

    private View headView;

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
        Util.addMenuIcon(getTitleBar(), (MainActivity) getActivity());
    }

    /*@Nullable
    @Override
    public int getContentHeaderViewId() {
        return R.layout.frag_me_head;
    }*/

    @Override
    protected View createHeaderView() {
        headView=getLayoutInflater().inflate(R.layout.frag_me_head,null);
        return headView;
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
                .drawable(R.mipmap.form_ic_myelephant)
                .name("我的象数")
                .text("1象数")
                .related(RelatedId.my_elephant)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.line_gray)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_mycollection)
                .name("我的收藏")
                .related(RelatedId.my_collection)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_set)
                .name("设置")
                .related(RelatedId.set)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.line_gray)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_about)
                .name("关于")
                .related(RelatedId.about)
                .build());

    }


    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedid = getItem(position).getInt(TFormElem.related);
        switch (relatedid) {
            case RelatedId.my_elephant: {
                showToast("象数");
            }
            break;
            case RelatedId.my_collection: {
                showToast("收藏");
            }
            break;
            case RelatedId.set: {
                showToast("设置");
            }
            break;
            case RelatedId.about: {
                showToast("关于");
            }
            break;
        }
    }


}
