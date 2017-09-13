package yy.doctor.ui.frag;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.ui.activity.me.HelpAndFeedbackActivity;
import yy.doctor.ui.activity.me.MyCollectionActivity;
import yy.doctor.ui.activity.me.SettingsActivity;
import yy.doctor.ui.activity.me.StatsMeetActivity;
import yy.doctor.ui.activity.me.epn.EpnActivity;
import yy.doctor.ui.activity.me.profile.ProfileActivity;
import yy.doctor.ui.activity.me.unitnum.UnitNumActivity;
import yy.doctor.view.CircleProgressView;

/**
 * 我
 *
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeFrag extends BaseFormFrag {

    private NetworkImageView mIvAvatar;

    private TextView mTvName;
    private TextView mTvTitle;
    private TextView mTvHospital;
    private TextView mTvProgress;
    private CircleProgressView mProgressbar;

    @IntDef({
            RelatedId.meeting_statistics,
            RelatedId.my_attention,
            RelatedId.my_collection,
            RelatedId.my_epn,

            RelatedId.settings,
            RelatedId.help_and_feedback,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {

        int meeting_statistics = 0;
        int my_attention = 1;
        int my_collection = 2;
        int my_epn = 3;

        int settings = 4;
        int help_and_feedback = 5;
    }

    @Override
    public void initData() {
        super.initData();

        int paddingLeft = fitDp(58);
        int paddingRight = fitDp(12);

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_meeting_statistics)
                .name(R.string.attend_meeting_statistics)
                .related(RelatedId.meeting_statistics));

        addItem(Form.create(FormType.divider_margin)
                .paddingLeft(paddingLeft)
                .paddingRight(paddingRight));

        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_my_attention_unit_num)
                .name(R.string.my_attention)
                .related(RelatedId.my_attention));

        addItem(Form.create(FormType.divider_margin)
                .paddingLeft(paddingLeft)
                .paddingRight(paddingRight));

        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_my_collection)
                .name(R.string.my_collection)
                .related(RelatedId.my_collection));

        //String.format(getString(R.string.num_epn), Profile.inst().getInt(TProfile.credits));
        addItem(Form.create(FormType.divider_margin)
                .paddingLeft(paddingLeft)
                .paddingRight(paddingRight));

        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_my_epn)
                .name(R.string.epn)
                .text(String.format(getString(R.string.num_epn), Profile.inst().getInt(TProfile.credits)))
                .related(RelatedId.my_epn));

        addItem(Form.create(FormType.divider_large));

        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_settings)
                .name(R.string.settings)
                .related(RelatedId.settings));

        addItem(Form.create(FormType.divider_margin)
                .paddingLeft(paddingLeft)
                .paddingRight(paddingRight));

        addItem(Form.create(FormType.text_intent_me)
                .drawable(R.drawable.form_ic_help_and_feedback)
                .name(R.string.held_and_feedback)
                .related(RelatedId.help_and_feedback));
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addTextViewMid(R.string.profile_center);
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_me_header);
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvAvatar = findView(R.id.me_header_iv);
        mTvName = findView(R.id.me_header_tv_name);
        mTvTitle = findView(R.id.me_header_tv_title);
        mTvHospital = findView(R.id.me_header_tv_hospital);
        mProgressbar = findView(R.id.me_header_progress);
        mTvProgress = findView(R.id.me_header_tv_progress);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvName.setText(Profile.inst().getString(TProfile.linkman));
        mTvTitle.setText(Profile.inst().getString(TProfile.title));
        mTvHospital.setText(Profile.inst().getString(TProfile.hospital));
        int integrity = Profile.inst().getInt(TProfile.integrity);
        mProgressbar.setProgress(integrity);
        if (integrity == 100) {
            mTvProgress.setText("完成");
        } else {
            mTvProgress.setText(Profile.inst().getInt(TProfile.integrity) + "%");
        }

        setOnClickListener(R.id.layout_me_header);
        mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                .renderer(new CircleRenderer())
                .url(Profile.inst().getString(TProfile.headimg))
                .load();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_me_header: {
                //head的点击事件
                startActivity(ProfileActivity.class);
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {

        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.meeting_statistics: {
                startActivity(StatsMeetActivity.class);
            }
            break;
            case RelatedId.my_attention: {
                startActivity(UnitNumActivity.class);
            }
            break;
            case RelatedId.my_collection: {
                startActivity(MyCollectionActivity.class);
            }
            break;
            case RelatedId.my_epn: {
                startActivity(EpnActivity.class);
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

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        //修改个人资料
        if (type == NotifyType.profile_change) {
            mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                    .renderer(new CircleRenderer())
                    .url(Profile.inst().getString(TProfile.headimg))
                    //.renderer(new CornerRenderer(fitDp(15)))  圆角
                    .load();

            mTvName.setText(Profile.inst().getString(TProfile.linkman));
            mTvHospital.setText(Profile.inst().getString(TProfile.hospital));
            mTvTitle.setText(Profile.inst().getString(TProfile.title));
            int integrity = Profile.inst().getInt(TProfile.integrity);
            mProgressbar.setProgress(integrity);
            if (integrity == 100) {
                mTvProgress.setText("完成");
            } else {
                mTvProgress.setText(Profile.inst().getInt(TProfile.integrity) + "%");
            }
            getRelatedItem(RelatedId.my_epn).text(String.format(getString(R.string.num_epn), Profile.inst().getInt(TProfile.credits)));
            refreshRelatedItem(RelatedId.my_epn);
        }
    }
}
