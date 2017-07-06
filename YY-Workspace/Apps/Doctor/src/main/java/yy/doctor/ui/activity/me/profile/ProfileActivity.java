package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.config.GlConfig;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.model.me.UpHeadImage;
import yy.doctor.model.me.UpHeadImage.TUpHeadImage;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.register.HospitalActivity;
import yy.doctor.ui.activity.register.ProvinceActivity;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

import static yy.doctor.model.Profile.TProfile.city;
import static yy.doctor.model.Profile.TProfile.cmeId;
import static yy.doctor.model.Profile.TProfile.department;
import static yy.doctor.model.Profile.TProfile.hosLevel;
import static yy.doctor.model.Profile.TProfile.hospital;
import static yy.doctor.model.Profile.TProfile.licence;
import static yy.doctor.model.Profile.TProfile.linkman;
import static yy.doctor.model.Profile.TProfile.province;
import static yy.doctor.model.Profile.TProfile.zone;

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

    private static final int KReqUpHeaderImgId = 10;
    private static final int KReqModifyId = 20;

    private static final String KPhotoCameraPrefix = "avatar";

    private RelativeLayout mLayoutProfileHeader;
    private NetworkImageView mIvAvatar;

    private Bitmap mBmp;
    private Bitmap mCircleBmp;
    private String mStrPhotoPath;
    private String mAvatarUrl;
    private String mStrProvince;
    private String mStrCity;
    private String mStrArea;

    @IntDef({
            RelatedId.name,
            RelatedId.hospital,
            RelatedId.departments,
            RelatedId.hospital_grade,

            RelatedId.nickname,
            RelatedId.phone_number,
            RelatedId.email,

            RelatedId.CME_number,
            RelatedId.certification_number,
            RelatedId.title,
            RelatedId.position,
            RelatedId.sex,
            RelatedId.education_background,
            RelatedId.address,

            RelatedId.is_open,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int name = 0;
        int hospital = 1;
        int departments = 2;
        int hospital_grade = 3;

        int nickname = 10;
        int phone_number = 11;
        int email = 12;

        int CME_number = 20;
        int certification_number = 21;
        int title = 22;
        int position = 23;
        int sex = 24;
        int education_background = 25;
        int address = 26;

        int is_open = 30;
    }

    @Override
    public void initData() {
        super.initData();

        mAvatarUrl = Profile.inst().getString(TProfile.headimg);

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.et)
                .related(RelatedId.name)
                .name(R.string.user_name)
                .text(Profile.inst().getString(linkman))
                .hint(R.string.required)
                .enable(false)
                .build());

        Place place = new Place();
        place.put(TPlace.province, getString(R.string.guang_dong));
        place.put(TPlace.city, getString(R.string.guang_zhou));

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_intent)
                .related(RelatedId.hospital)
                .drawable(R.mipmap.form_ic_more)
                .name(R.string.user_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, place))
                .text(Profile.inst().getString(hospital))
                .hint(R.string.required)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_intent)
                .related(RelatedId.departments)
                .drawable(R.mipmap.form_ic_more)
                .name(R.string.user_section)
                .intent(new Intent(this, SectionActivity.class))
                .text(Profile.inst().getString(department))
                .hint(R.string.required)
                .build());

        YSLog.d(TAG, "hospital level = " + Profile.inst().getString(hosLevel));
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.text_dialog)
                .related(RelatedId.hospital_grade)
                .name(R.string.user_hospital_grade)
                .text(Profile.inst().getString(hosLevel))
                .data(GlConfig.inst().getHospitalGrade())
                .hint(R.string.optional)
                .build());

        /*addItem(new Builder(FormType.et)
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
        addItem(new Builder(FormType.divider_large).build());*/

        /*addItem(new Builder(FormType.profile_checkbox)
                .related(RelatedId.is_open)
                .build());*/

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.text_intent)
                .related(RelatedId.title)
                .name(R.string.user_title)
                .intent(new Intent(this, TitleActivity.class))
                .text(Profile.inst().getString(TProfile.title))
                .hint(R.string.optional)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et)
                .related(RelatedId.certification_number)
                .name(R.string.user_certification_number)
                .text(Profile.inst().getString(licence))
                .hint(R.string.optional)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et)
                .related(RelatedId.CME_number)
                .name(R.string.user_CME_number)
                .text(Profile.inst().getString(cmeId))
                .hint(R.string.optional)
                .build());

        /*addItem(new Builder(FormType.et)
                .related(RelatedId.position)
                .name("职务")
                .text(Profile.inst().getString(place))
                .hint(R.string.hint_not_fill)
                .build());
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.text_dialog)
                .related(RelatedId.sex)
                .name("性别")
                .text(R.string.hint_not_fill)
                .data(GlConfig.inst().getSexConfigs())
                .build());
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.text_dialog)
                .related(RelatedId.education_background)
                .name("学历")
                .text(R.string.hint_not_fill)
                .data(GlConfig.inst().getEducationBgConfigs())
                .build());
        addItem(new Builder(FormType.divider).build());*/

        String str;
        mStrProvince = Profile.inst().getString(province);
        mStrCity = Profile.inst().getString(city);
        mStrArea = Profile.inst().getString(zone);
        if (mStrArea != null) {
            str = mStrProvince + " " + mStrCity + " " + mStrArea;
        } else {
            str = mStrProvince + " " + mStrCity;
        }
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.text_intent)
                .related(RelatedId.address)
                .name(R.string.user_city)
                .intent(new Intent(this, ProvinceActivity.class))
                .text(str)
                .hint(R.string.required)
                .build());

        addItem(new Builder(FormType.divider_large)
                .build());
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.user_profile, this);
        bar.addTextViewRight(R.string.user_save, v -> {
            refresh(RefreshWay.dialog);
            //如果头像修改了就先上传头像，成功后再修改其他资料
            if (mBmp != null) {
                exeNetworkReq(KReqUpHeaderImgId, NetFactory.upheadimg(BmpUtil.toBytes(mBmp)));
            } else {
                modify();
            }
        });
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
        mIvAvatar.placeHolder(R.mipmap.ic_default_user_header)
                .url(mAvatarUrl)
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

    private void showDialogSelectPhoto() {

        final BottomDialog dialog = new BottomDialog(this, position -> {

            switch (position) {
                case 0: {
                    if (checkPermission(KPermissionCodeAlbum, Permission.storage)) {
                        getPhotoFromAlbum();
                    }
                }
                break;
                case 1: {
                    if (checkPermission(KPermissionCodePhoto, Permission.camera)) {
                        getPhotoFromCamera();
                    }
                }
                break;
            }
        });

        dialog.addItem(getString(R.string.from_album_select), KColorNormal);
        dialog.addItem(getString(R.string.take_photo), KColorNormal);
        dialog.addItem(getString(R.string.cancel), KColorCancel);

        dialog.show();
    }

    private void getPhotoFromAlbum() {
        PhotoUtil.fromAlbum(this, KCodeAlbum);
    }

    private void getPhotoFromCamera() {
        mStrPhotoPath = CacheUtil.getUploadCacheDir() + KPhotoCameraPrefix + System.currentTimeMillis() + CacheUtil.KJpgExtend;
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
                startAcForResult(path);
            }
            break;
            case KCodePhotograph: {
                // 通过照相机拍的图片
                path = mStrPhotoPath;
                startAcForResult(path);
            }
            break;
            case KCodeClipImage:
                mBmp = ClipImageActivity.mBmp;
                mCircleBmp = BmpUtil.toCircle(mBmp);
                mIvAvatar.setImageBitmap(mCircleBmp);
                //YSLog.d(TAG, "mBmp.getByteCount() = " + mBmp.getByteCount());
                break;
        }
    }

    //页面跳转
    private void startAcForResult(String path) {
        if (path != null) {
            Intent intent = new Intent(this, ClipImageActivity.class);
            intent.putExtra(Extra.KData, path);
            startActivityForResult(intent, KCodeClipImage);
        }
    }

    /**
     * 修改个人资料
     */
    private void modify() {

        String str = getRelatedItem(RelatedId.address).getString(TFormElem.text);
        YSLog.d(TAG, "省市 = " + str);
        mStrProvince = str.substring(0, str.indexOf(" "));
        String[] addresses = str.split(" ");
        mStrProvince = addresses[0];
        mStrCity = addresses[1];
        mStrArea = "";
        if (addresses.length == 3) {
            mStrArea = addresses[2];
        }

        YSLog.d(TAG, "province = " + mStrProvince);
        YSLog.d(TAG, "city = " + mStrCity);
        YSLog.d(TAG, "area = " + mStrArea);

        YSLog.d(TAG, "success hospital level = " + getRelateVal(RelatedId.hospital_grade));
        NetworkReq r = NetFactory.newModifyBuilder()
                .headImgUrl(mAvatarUrl)
                .linkman(getRelateVal(RelatedId.name))
                .hospital(getRelateVal(RelatedId.hospital))
                .department(getRelateVal(RelatedId.departments))
                .hospitalLevel(getRelateVal(RelatedId.hospital_grade))
                .cmeId(getRelateVal(RelatedId.CME_number))
                .licence(getRelateVal(RelatedId.certification_number))
                .title(getRelateVal(RelatedId.title))
                .province(mStrProvince)
                .city(mStrCity)
                .area(mStrArea)
                .builder();
        refresh(RefreshWay.dialog);
        exeNetworkReq(KReqModifyId, r);
    }

    private String getRelateVal(@RelatedId int relateId) {
        return getRelatedItem(relateId).getString(TFormElem.val);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KReqUpHeaderImgId) {
            return JsonParser.ev(r.getText(), UpHeadImage.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        if (id == KReqUpHeaderImgId) {
            Result<UpHeadImage> r = (Result<UpHeadImage>) result;
            if (r.isSucceed()) {
                UpHeadImage upHeadImage = r.getData();
                mAvatarUrl = upHeadImage.getString(TUpHeadImage.url);

                YSLog.d(TAG, "onNetworkSuccess: 头像设置成功 = " + mAvatarUrl);
                //头像路径保存到本地
                Profile.inst().update(Profile.inst().put(TProfile.headimg, mAvatarUrl));
                modify();
            } else {
                onNetworkError(id, new NetError(id, r.getError()));
            }
        } else {
            stopRefresh();
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast(ResLoader.getString(R.string.user_save_success));
                //更新本地的数据
                Profile.inst().put(TProfile.hospital, getRelateVal(RelatedId.hospital));
                Profile.inst().put(TProfile.department, getRelateVal(RelatedId.departments));
                Profile.inst().put(TProfile.hosLevel, getRelateVal(RelatedId.hospital_grade));
                Profile.inst().put(TProfile.title, getRelateVal(RelatedId.title));
                Profile.inst().put(TProfile.licence, getRelateVal(RelatedId.certification_number));
                Profile.inst().put(TProfile.cmeId, getRelateVal(RelatedId.CME_number));

                Profile.inst().put(TProfile.province, mStrProvince);
                Profile.inst().put(TProfile.city, mStrCity);
                if (TextUtil.isEmpty(mStrArea)) {
                    Profile.inst().put(TProfile.zone, "");
                } else {
                    Profile.inst().put(TProfile.zone, mStrArea);
                }
                Profile.inst().saveToSp();

                notify(NotifyType.profile_change);
            } else {
                showToast(r.getError());
            }
        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        if (code == KPermissionCodePhoto) {
            switch (result) {
                case PermissionResult.granted: {
                    getPhotoFromCamera();
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    showToast(getString(R.string.user_photo_permission));
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
                    showToast(ResLoader.getString(R.string.user_album_permission));
                }
                break;
            }
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.province_finish) {
            Place place = (Place) data;
            mStrProvince = place.getString(TPlace.province);
            mStrCity = place.getString(TPlace.city);
            mStrArea = place.getString(TPlace.district);
            String str;
            if (mStrArea != null) {
                str = mStrProvince + " " + mStrCity + " " + mStrArea;
            } else {
                str = mStrProvince + " " + mStrCity;
            }
            getRelatedItem(RelatedId.address).put(TFormElem.text, str);
            refreshRelatedItem(RelatedId.address);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //回收bitmap
        ClipImageActivity.recycleBmp();
        BmpUtil.recycle(mCircleBmp);
    }

}
