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

            RelatedId.is_open,
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

        int is_open = 20;

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
                .related(RelatedId.name)
                .name("姓名")
                .text("sdfsdf")
                .enable(false)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.hospital)
                .drawable(R.mipmap.ic_arrow_right)
                .name("医院")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.major)
                .name("科室")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.nickname)
                .name("昵称")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.phone_number)
                .name("手机号")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.email)
                .name("电子邮箱")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.profile_checkbox)
                .related(RelatedId.is_open)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.certification_number)
                .name("职业资格证号")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.rank)
                .name("职称")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.position)
                .name("职务")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.sex)
                .name("性别")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.education_background)
                .name("学历")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.address)
                .name("所在城市")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.divider_large).build());

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
