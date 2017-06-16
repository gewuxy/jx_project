package yy.doctor.frag;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.yy.Notifier.NotifyType;
import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.activity.me.CollectionMeetingActivity;
import yy.doctor.activity.me.epc.EpcActivity;
import yy.doctor.activity.me.epn.EpnActivity;
import yy.doctor.activity.me.HelpAndFeedbackActivity;
import yy.doctor.activity.me.profile.ProfileActivity;
import yy.doctor.activity.me.SettingsActivity;
import yy.doctor.activity.me.unitnum.UnitNumActivity;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

import static yy.doctor.model.Profile.TProfile.department;
import static yy.doctor.model.Profile.TProfile.hospital;
import static yy.doctor.model.Profile.TProfile.linkman;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeFrag extends BaseFormFrag {

    private NetworkImageView mIvAvatar;

    private TextView mTvName;
    private TextView mTvHospital;

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
    public void initNavBar(NavBar bar) {

        bar.addTextViewMid("个人中心");

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
                .drawable(R.mipmap.form_ic_my_attention_unit_num)
                .name("单位号")
                .related(RelatedId.my_attention)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_my_collection)
                .name("会议收藏")
                .related(RelatedId.my_collection)
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.content)
                .drawable(R.mipmap.form_ic_my_epn)
                .name("象数")
                .text(Profile.inst().getString(TProfile.credits) + "象数")
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

        mIvAvatar = findView(R.id.me_header_iv);
        mTvName = findView(R.id.me_header_tv_name);
        mTvHospital = findView(R.id.me_header_tv_hospital);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvName.setText(Profile.inst().getString(linkman) + "  " + Profile.inst().getString(department));
        mTvHospital.setText(Profile.inst().getString(hospital));

        setOnClickListener(R.id.layout_me_header);
        mIvAvatar.placeHolder(R.mipmap.ic_default_user_header)
                .renderer(new CircleRenderer())
                .url(Profile.inst().getString(TProfile.headimg))
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
                startActivity(UnitNumActivity.class);
            }
            break;
            case RelatedId.my_collection: {
                startActivity(CollectionMeetingActivity.class);
            }
            break;
            case RelatedId.my_epn: {
                startActivity(EpnActivity.class);
            }
            break;
            case RelatedId.epc: {
                startActivity(EpcActivity.class);
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

    @Override
    protected boolean useLazyLoad() {
        return true;
    }

    //修改个人资料
    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.profile_change) {
            mIvAvatar.placeHolder(R.mipmap.ic_default_user_header)
                    .renderer(new CircleRenderer())
                    .url(Profile.inst().getString(TProfile.headimg))
                    //.renderer(new CornerRenderer(fitDp(15)))  圆角
                    .load();
            mTvName.setText(Profile.inst().getString(linkman) + "  " + Profile.inst().getString(department));
            mTvHospital.setText(Profile.inst().getString(hospital));
            getRelatedItem(RelatedId.my_epn).put(TFormElem.text, Profile.inst().getString(TProfile.credits) + "象数");
            refreshRelatedItem(RelatedId.my_epn);
        }
    }
}
