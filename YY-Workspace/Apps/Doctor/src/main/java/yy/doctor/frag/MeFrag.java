package yy.doctor.frag;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.LinearLayout;

import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.activity.me.AboutActivity;
import yy.doctor.activity.me.MyCollectionActivity;
import yy.doctor.activity.me.MyEpnActivity;
import yy.doctor.activity.me.ProfileActivity;
import yy.doctor.activity.me.SettingsActivity;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeFrag extends BaseFormFrag {

    private LinearLayout mLayoutHeader;
    private NetworkImageView mIvAvatar;

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
        return inflate(R.layout.layout_me_header);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.main_bg)
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
                .backgroundRes(R.color.main_bg)
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
                .backgroundRes(R.color.main_bg)
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

        mLayoutHeader = findView(R.id.layout_me_header);
        mIvAvatar = findView(R.id.me_header_iv);

    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mLayoutHeader.setOnClickListener(this);
        mIvAvatar.placeHolder(R.mipmap.form_ic_personal_head)
                .renderer(new CircleRenderer())
                //.renderer(new CornerRenderer(fitDp(15)))  圆角
                .load();

    }

    //head的点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_me_header: {
                startActivity(ProfileActivity.class);
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.my_elephant: {
                Intent intent = new Intent(getContext(), MyEpnActivity.class);
                startActivity(intent);
            }
            break;
            case RelatedId.my_collection: {
                Intent intent = new Intent(getContext(), MyCollectionActivity.class);
                startActivity(intent);
            }
            break;
            case RelatedId.set: {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
            break;
            case RelatedId.about: {
                Intent intent = new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
            }
            break;
        }
    }


}
