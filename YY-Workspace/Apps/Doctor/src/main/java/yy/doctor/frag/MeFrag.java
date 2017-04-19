package yy.doctor.frag;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.LinearLayout;

import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.activity.me.HelpAndFeedbackActivity;
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
            RelatedId.my_attention,
            RelatedId.my_collection,

            RelatedId.my_epn,
            RelatedId.epc,

            RelatedId.settings,
            RelatedId.help_and_feedback,
    })
    private @interface RelatedId {

        int my_attention = 0;
        int my_collection = 1;

        int my_epn = 2;
        int epc = 3;

        int settings = 4;
        int help_and_feedback = 5;
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

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_my_attention)
                .name("关注的单位号")
                .related(RelatedId.my_attention)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_my_collection)
                .name("收藏会议")
                .related(RelatedId.my_collection)
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_my_epn)
                .name("我的象数")
                .text("1象数")
                .related(RelatedId.my_epn)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_epc)
                .name("象城")
                .related(RelatedId.epc)
                .build());


        addItem(new Builder(FormType.divider_large).build());


        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_settings)
                .name("设置")
                .related(RelatedId.settings)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_help_and_feedback)
                .name("帮助与反馈")
                .related(RelatedId.help_and_feedback)
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

            case RelatedId.my_attention: {
                showToast("555");
            }
            break;
            case RelatedId.my_collection: {
                startActivity(MyCollectionActivity.class);
            }
            break;
            case RelatedId.my_epn: {
                startActivity(MyEpnActivity.class);
            }
            break;
            case RelatedId.epc: {
                showToast("999");
            }
            break;
            case RelatedId.settings: {
                startActivity(SettingsActivity.class);
            }
            break;
            case RelatedId.help_and_feedback: {
                startActivity(HelpAndFeedbackActivity.class);
            }
            break;
        }
    }


}
