package yy.doctor.frag;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.RelativeLayout;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.activity.AboutActivity;
import yy.doctor.activity.MainActivity;
import yy.doctor.activity.MyCollectionActivity;
import yy.doctor.activity.MyElephantNumActivity;
import yy.doctor.activity.SettingsActivity;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeFrag extends BaseFormFrag {

    private RelativeLayout mHeadRy;
    //private LinearLayout mAttentionLy, mFriendsLy, mFansLy;
    //private TextView mAttentionNum, mFriendsNum, mFansNum;

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

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.frag_me_head);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_myelephant)
                .name("我的象数")
                .text("1象数")
                .related(RelatedId.my_elephant)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_mycollection)
                .name("我的收藏")
                .related(RelatedId.my_collection)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_set)
                .name("设置")
                .related(RelatedId.set)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.line_large)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_about)
                .name("关于")
                .related(RelatedId.about)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

    }

    @Override
    public void findViews() {
        super.findViews();

        mHeadRy = findView(R.id.me_layout_head);

        /*mAttentionLy = findView(R.id.me_attention);
        mFriendsLy = findView(R.id.me_friends);
        mFansLy = findView(R.id.me_fans);

        mAttentionNum = findView(R.id.attention_num);
        mFriendsNum = findView(R.id.friends_num);
        mFansNum = findView(R.id.fans_num);*/

    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mHeadRy.setOnClickListener(this);

        /*mAttentionLy.setOnClickListener(this);
        mFriendsLy.setOnClickListener(this);
        mFansLy.setOnClickListener(this);*/

    }

    //head的点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.me_layout_head: {
                showToast("资料");
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.my_elephant: {
                Intent intent=new Intent(getContext(), MyElephantNumActivity.class);
                startActivity(intent);
            }
            break;
            case RelatedId.my_collection: {
                Intent intent=new Intent(getContext(), MyCollectionActivity.class);
                startActivity(intent);
            }
            break;
            case RelatedId.set: {
                Intent intent=new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
            break;
            case RelatedId.about: {
                Intent intent=new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
            }
            break;
        }
    }


}
