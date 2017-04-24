package yy.doctor.activity.me;

import android.support.annotation.IntDef;
import android.view.View;

import lib.ys.ex.NavBar;
import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * 收藏会议
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class CollectionMeetingActivity extends BaseFormActivity {

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
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "收藏的会议", this);

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider).build());

    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.post: {
                showToast("帖子");
            }
        }

    }

}
