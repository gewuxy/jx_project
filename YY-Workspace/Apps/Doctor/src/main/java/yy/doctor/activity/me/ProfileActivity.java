package yy.doctor.activity.me;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseFormActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.BottomDialog.OnDialogItemClickListener;
import yy.doctor.model.Modify;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

import static yy.doctor.model.Profile.TProfile.city;
import static yy.doctor.model.Profile.TProfile.department;
import static yy.doctor.model.Profile.TProfile.hospital;
import static yy.doctor.model.Profile.TProfile.licence;
import static yy.doctor.model.Profile.TProfile.linkman;
import static yy.doctor.model.Profile.TProfile.mobile;
import static yy.doctor.model.Profile.TProfile.nickname;
import static yy.doctor.model.Profile.TProfile.place;
import static yy.doctor.model.Profile.TProfile.province;
import static yy.doctor.model.Profile.TProfile.title;
import static yy.doctor.model.Profile.TProfile.username;


/**
 * 我的资料
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class ProfileActivity extends BaseFormActivity {

    private static final int KColorNormal = Color.parseColor("#666666");
    private static final int KColorCancel = Color.parseColor("#01b557");

    private static final int KCodeAlbum = 100;
    private static final int KCodePhotograph = 200;
    private static final int KCodeClipImage = 300;

    private static final int KPermissionCodePhoto = 0;
    private static final int KPermissionCodeAlbum = 1;


    private static final String KPhotoFileName = "avatar.jpg";

    private RelativeLayout mLayoutProfileHeader;
    private NetworkImageView mIvAvatar;

    private String mStrPhotoPath;
    private String mAvatarUrl;
    private String mStrProvince;
    private String mStrCity;

    @IntDef({
            RelatedId.name,
            RelatedId.hospital,
            RelatedId.departments,

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
        int departments = 2;

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
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.name)
                .name("姓名")
                .text(Profile.inst().getString(linkman))
                .hint(R.string.hint_not_fill)
                .enable(false)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.hospital)
                .drawable(R.mipmap.ic_more)
                .name("医院")
                .text(Profile.inst().getString(hospital))
                .hint(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        String strDepartments = Profile.inst().getString(department);
        if (strDepartments.equals("")) {
            strDepartments = ResLoader.getString(R.string.hint_not_fill);
        }
        addItem(new Builder(FormType.et_intent)
                .related(RelatedId.departments)
                .drawable(R.mipmap.ic_more)
                .name("科室")
                .intent(new Intent(this, SectionActivity.class))
                .text(strDepartments)
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.nickname)
                .name("昵称")
                .text(Profile.inst().getString(nickname))
                .hint(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.phone_number)
                .name("手机号")
                .text(Profile.inst().getString(mobile))
                .hint(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.email)
                .name("电子邮箱")
                .text(Profile.inst().getString(username))
                .hint(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider_large).build());

        /*addItem(new Builder(FormType.profile_checkbox)
                .related(RelatedId.is_open)
                .build());*/

        addItem(new Builder(FormType.et)
                .related(RelatedId.certification_number)
                .name("职业资格证号")
                .text(Profile.inst().getString(licence))
                .hint(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.rank)
                .name("职称")
                .text(Profile.inst().getString(title))
                .hint(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et)
                .related(RelatedId.position)
                .name("职务")
                .text(Profile.inst().getString(place))
                .hint(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.text)
                .related(RelatedId.sex)
                .name("性别")
                .text(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.text)
                .related(RelatedId.education_background)
                .name("学历")
                .text(R.string.hint_not_fill)
                .build());

        addItem(new Builder(FormType.divider).build());

        mStrProvince = Profile.inst().getString(province);
        mStrCity = Profile.inst().getString(city);
        addItem(new Builder(FormType.text_intent)
                .related(RelatedId.address)
                .name("所在城市")
                .intent(new Intent(this, ProvinceCityActivity.class))
                .text(mStrProvince + "-" + mStrCity)
                .build());

        addItem(new Builder(FormType.divider_large)
                .build());
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "我的资料", this);
        bar.addTextViewRight("完成并保存", v -> modify());

    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_profile_header);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutProfileHeader = findView(R.id.layout_profile_header);
        mIvAvatar = findView(R.id.profile_header_iv_avatar);

    }

    @Override
    public void setViews() {
        super.setViews();

        mLayoutProfileHeader.setOnClickListener(this);
        mIvAvatar.placeHolder(R.mipmap.form_ic_personal_head)
                .renderer(new CircleRenderer())
                .load();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.layout_profile_header: {
                showDialogSelectPhoto();
            }
            break;
        }

    }

    @Override
    protected void onFormItemClick(View v, int position) {

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.sex: {
                showDialogSelectSex(position);
            }
            break;
            case RelatedId.education_background: {
                showDialogSelectEducationBackground(position);
            }
            break;
        }
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        super.onFormViewClick(v, position, related);

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);

        if (v instanceof ImageView) {

            switch (relatedId) {
                case RelatedId.hospital: {
                    showToast("852");
                }
                break;
            }
        }

    }

    private void showDialogSelectPhoto() {

        final List<String> data = new ArrayList<>();
        data.add("从相册中选择照片");
        data.add("拍照");
        data.add("取消");

        final BottomDialog dialog = new BottomDialog(this, new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {

                switch (position) {
                    case 0: {
                        if (checkPermission(KPermissionCodePhoto, Permission.storage)) {
                            getPhotoFromAlbum();
                        }
                    }
                    break;
                    case 1: {
                        if (checkPermission(KPermissionCodePhoto, Permission.camera)) {
                            getPhotoFromTakePhoto();
                        }
                    }
                    break;
                }

            }
        });

        for (int i = 0; i < data.size(); ++i) {

            if (i != (data.size() - 1)) {
                dialog.addItem(data.get(i), KColorNormal);
            } else {
                dialog.addItem(data.get(i), KColorCancel);
            }
        }

        dialog.show();

    }

    private void getPhotoFromAlbum() {
        PhotoUtil.fromAlbum(this, KCodeAlbum);
    }

    private void getPhotoFromTakePhoto() {
        mStrPhotoPath = CacheUtil.getBmpCacheDir() + KPhotoFileName;
        PhotoUtil.fromCamera(this, mStrPhotoPath, KCodePhotograph);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        String path = null;
        switch (requestCode) {
            case KCodeAlbum: {
                // 查看相册获得图片返回
                path = PhotoUtil.getPath(this, data.getData());
            }
            break;
            case KCodePhotograph: {
                // 通过照相机拍的图片
                path = mStrPhotoPath;
            }
            break;
        }

        if (path != null) {
            Intent intent = new Intent(this, ClipImageActivity.class);
            intent.putExtra(Extra.KData, path);
            startActivity(intent);
        }
    }

    private void showDialogSelectSex(final int pos) {

        final List<String> data = new ArrayList<>();
        data.add("男");
        data.add("女");

        final BottomDialog dialog = new BottomDialog(this, new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {

                getItem(pos).put(TFormElem.text, data.get(position));
                refreshItem(pos);
            }
        });

        for (int i = 0; i < data.size(); ++i) {
            dialog.addItem(data.get(i), KColorNormal);
        }

        dialog.show();

    }

    private void showDialogSelectEducationBackground(final int pos) {

        final List<String> data = new ArrayList<>();
        data.add("专科");
        data.add("本科");
        data.add("硕士");
        data.add("博士");
        data.add("博士后");

        final BottomDialog dialog = new BottomDialog(this, new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {

                getItem(pos).put(TFormElem.text, data.get(position));
                refreshItem(pos);
            }
        });

        for (int i = 0; i < data.size(); ++i) {
            dialog.addItem(data.get(i), KColorNormal);
        }

        dialog.show();

    }

    private String getRelateVal(@RelatedId int relateId) {
        return getRelatedItem(relateId).getString(TFormElem.val);
    }

    /**
     * 修改个人资料
     */
    private void modify() {

        String str = getRelatedItem(RelatedId.address).getString(TFormElem.text);
        mStrProvince = str.substring(0, str.indexOf("-") - 1);
        mStrCity = str.substring(str.indexOf("-") + 1, str.length());

        NetworkReq r = NetFactory.newModifyBuilder()
                .nickname(getRelateVal(RelatedId.nickname))
                .linkman(getRelateVal(RelatedId.name))
                .mobile(getRelateVal(RelatedId.phone_number))
                .province(mStrProvince)
                .city(mStrCity)
                .hospital(getRelateVal(RelatedId.hospital))
                .department(getRelateVal(RelatedId.departments))
                .builder();

        refresh(RefreshWay.dialog);
        exeNetworkReq(1, r);

    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Modify.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();

        Result<Modify> r = (Result<Modify>) result;
        if (r.isSucceed()) {
            showToast("资料修改成功");
            Profile.inst().update(Profile.inst().put(TProfile.province, mStrProvince));
            Profile.inst().update(Profile.inst().put(TProfile.city, mStrCity));
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        if (code == KPermissionCodePhoto) {
            switch (result) {
                case PermissionResult.granted: {
                    getPhotoFromTakePhoto();
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    showToast("请开启拍照权限");
                }
                break;
            }
        } else if (code == KPermissionCodeAlbum) {
            switch (result) {
                case PermissionResult.granted: {
                    getPhotoFromAlbum();
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    showToast("请开启查看相册权限");
                }
                break;
            }
        }
    }

}
