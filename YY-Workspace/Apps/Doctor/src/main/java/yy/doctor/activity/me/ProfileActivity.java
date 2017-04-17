package yy.doctor.activity.me;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.RelativeLayout;

import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

/**
 * 资料展示
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class ProfileActivity extends BaseFormActivity {

    private RelativeLayout mLayoutProfileHeader;
    private NetworkImageView mIvAvator;

    @IntDef({
            RelatedId.name,
            RelatedId.hospital,
            RelatedId.major,

            RelatedId.nickname,
            RelatedId.phone_number,
            RelatedId.email,

            RelatedId.certification_number,
            RelatedId.rank,
            RelatedId.position,
            RelatedId.sex,
            RelatedId.education_background,
            RelatedId.address,

            RelatedId.isopen,
    })
    private @interface RelatedId {

        int name = 0;
        int hospital = 1;
        int major = 2;

        int nickname = 3;
        int phone_number = 4;
        int email = 5;

        int certification_number = 6;
        int rank = 7;
        int position = 8;
        int sex = 9;
        int education_background = 10;
        int address = 11;

        int isopen = 20;

    }

    @Override
    public void initTitleBar() {

    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_profile_header);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.et)
                .name("姓名")
                .text("sdfsdf")
                .enable(false)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .drawable(R.mipmap.ic_arrow_right)
                .name("医院")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .name("科室")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .name("昵称")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("手机号")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("电子邮箱")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.profile_checkbox)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("职业资格证号")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("职称")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("职务")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("性别")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("学历")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.et)
                .name("所在城市")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.app_bg)
                .build());

    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutProfileHeader = findView(R.id.layout_profile_header);
        mIvAvator = findView(R.id.profile_header_iv_avatar);

    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mLayoutProfileHeader.setOnClickListener(this);
        mIvAvator.placeHolder(R.mipmap.form_ic_personal_head)
                .renderer(new CircleRenderer())
                .load();

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.layout_profile_header: {
                showToast("852");
            }
            break;
        }

    }

    @Override
    protected void onFormItemClick(View v, int position) {

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.name: {
                showToast("965");
            }
            break;
        }

    }


}
