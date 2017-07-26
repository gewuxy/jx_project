package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.text.intent.IntentForm.IntentType;
import yy.doctor.ui.activity.HospitalActivity;
import yy.doctor.ui.activity.register.ProvinceActivity;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

import static yy.doctor.model.Profile.TProfile.cmeId;
import static yy.doctor.model.Profile.TProfile.department;
import static yy.doctor.model.Profile.TProfile.hospital;
import static yy.doctor.model.Profile.TProfile.licence;
import static yy.doctor.model.Profile.TProfile.linkman;
import static yy.doctor.model.Profile.TProfile.specialized;
import static yy.doctor.model.Profile.TProfile.title;

/**
 * 我的资料
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class ProfileActivity extends BaseFormActivity {

    private final int KColorNormal = Color.parseColor("#666666");
    private final int KColorCancel = Color.parseColor("#01b557");

    private final int KBaseCode = 1000;
    private final int KCodeAlbum = KBaseCode + 1;
    private final int KCodePhotograph = KBaseCode + 2;
    private final int KCodeClipImage = KBaseCode + 3;

    private final int KPermissionCodePhoto = 0;
    private final int KPermissionCodeAlbum = 1;

    private final String KPhotoCameraPrefix = "avatar";

    private RelativeLayout mLayoutProfileHeader;
    private NetworkImageView mIvAvatar;

    private Bitmap mBmp;
    private Bitmap mCircleBmp;
    private String mStrPhotoPath;
    private String mAvatarUrl;

    private ProgressBar mProgressBar;
    private TextView mTvPercent;
    private int mProgressProFile = 0;


    @IntDef({
            RelatedId.name,
            RelatedId.hospital,
            RelatedId.departments,
            RelatedId.hospital_grade,

            RelatedId.nickname,
            RelatedId.phone_number,
            RelatedId.email,

            RelatedId.cme_number,
            RelatedId.certification_number,
            RelatedId.title,
            RelatedId.position,
            RelatedId.sex,
            RelatedId.education_background,
            RelatedId.address,

            RelatedId.specialized,
            RelatedId.skill,

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

        int cme_number = 20;
        int certification_number = 21;
        int title = 22;
        int position = 23;
        int sex = 24;
        int education_background = 25;
        int address = 26;

        int specialized = 27;

        int is_open = 30;
        int skill = 32;
    }

    @Override
    public void initData() {
        super.initData();

        mAvatarUrl = Profile.inst().getString(TProfile.headimg);
        if (!TextUtil.isEmpty(mAvatarUrl)) {
            mProgressProFile += 10;
            YSLog.d("qqq", "1完整度" + mProgressProFile);
        }

        addItem(Form.create(FormType.divider_large));
        if (!TextUtil.isEmpty(Profile.inst().getString(linkman))) {
            mProgressProFile += 10;
            YSLog.d("qqq", "2完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.et)
                .related(RelatedId.name)
                .name(R.string.user_name)
                .text(Profile.inst().getString(linkman))
                .hint(R.string.required)
                .enable(false));

        // FIXME: 是否默认显示定位城市
        Place place = new Place();
        place.put(TPlace.province, getString(R.string.guang_dong));
        place.put(TPlace.city, getString(R.string.guang_zhou));

        addItem(Form.create(FormType.divider));
        if (!TextUtil.isEmpty(Profile.inst().getString(hospital))) {
            mProgressProFile += 10;
            YSLog.d("qqq", "3完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.hospital)
//                .drawable(R.mipmap.form_ic_more)
                .name(R.string.user_hospital)
                .intent(new Intent(this, HospitalActivity.class))
                .mode(IntentType.hospital)
                .text(Profile.inst().getString(hospital))
                .hint(R.string.choose_hospital));

        addItem(Form.create(FormType.divider));
        if (!TextUtil.isEmpty(Profile.inst().getString(specialized))) {
            mProgressProFile += 10;
            YSLog.d("qqq", "4完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.specialized)
                .name(R.string.user_section)
                .intent(ModifyTextActivity.newIntent(this, getString(R.string.user_section), TProfile.specialized))
                .text(Profile.inst().getString(specialized))
                .hint(R.string.user_input_section));

        addItem(Form.create(FormType.divider));
        if (!TextUtil.isEmpty(place.toString())) {
            mProgressProFile += 10;
            YSLog.d("qqq", "5完整度" + mProgressProFile);
        }

        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.address)
                .name(R.string.user_city)
                .intent(new Intent(this, ProvinceActivity.class).putExtra(Extra.KData, IntentType.location))
                .text(place.toString())
                .hint(R.string.province_city_district));

        /*YSLog.d(TAG, "hospital level = " + Profile.inst().getString(hosLevel));
        addItem(new Builder(FormType.divider));
        addItem(new Builder(FormType.text_dialog)
                .related(RelatedId.hospital_grade)
                .name(R.string.user_hospital_grade)
                .text(Profile.inst().getString(hosLevel))
                .data(GlConfig.inst().getHospitalGrade())
                .hint(R.string.optional)
                );*/


        addItem(Form.create(FormType.divider_large));
        if (!TextUtil.isEmpty(Profile.inst().getString(department))) {
            mProgressProFile += 10;
            YSLog.d("qqq", "6完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.departments)
                .intent(new Intent(this, SectionActivity.class)
                        .putExtra(Extra.KData, IntentType.medicine))
                .name(R.string.specialized)
                .text(Profile.inst().getString(department))
                .hint(R.string.user_input_Specialist));

        addItem(Form.create(FormType.divider));
        if (!TextUtil.isEmpty(Profile.inst().getString(cmeId))) {
            mProgressProFile += 10;
            YSLog.d("qqq", "7完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.cme_number)
                .name(R.string.user_CME_number)
                .intent(ModifyTextActivity.newIntent(this, getString(R.string.user_CME_number), TProfile.cmeId))
                .text(Profile.inst().getString(TProfile.cmeId))
                .hint(R.string.user_input_CME_number));

        addItem(Form.create(FormType.divider));
        if (!TextUtil.isEmpty(Profile.inst().getString(licence))) {
            mProgressProFile += 10;
            YSLog.d("qqq", "8完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.certification_number)
                .name(R.string.user_certification_number)
                .intent(ModifyTextActivity.newIntent(this, getString(R.string.user_certification_number), TProfile.licence))
                .text(Profile.inst().getString(TProfile.licence))
                .hint(R.string.user_input_certification_number));

        addItem(Form.create(FormType.divider));
        if (!TextUtil.isEmpty(Profile.inst().getString(title))) {
            mProgressProFile += 10;
            YSLog.d("qqq", "9完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.title)
                .name(R.string.user_title)
                .intent(new Intent(this, TitleActivity.class).putExtra(Extra.KData, IntentType.doctor))
                .text(Profile.inst().getString(TProfile.title))
                .hint(R.string.user_title));

        addItem(Form.create(FormType.divider_large));
        if (!TextUtil.isEmpty(Profile.inst().getString(TProfile.academic))) {
            YSLog.d("www", Profile.inst().getString(TProfile.academic) + "rrr");
            mProgressProFile += 10;
            YSLog.d("qqq", "10完整度" + mProgressProFile);
        }
        addItem(Form.create(FormType.modify_intent_skill)
                .related(RelatedId.skill)
                .name(R.string.medical_skill)
                .intent(AcademicActivity.newIntent(this, getString(R.string.medical_skill), TProfile.academic))
                .text(Profile.inst().getString(TProfile.academic)));

    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.user_profile, this);
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
        mProgressBar = findView(R.id.profile_progressbar);
        mTvPercent = findView(R.id.profile_tv_percent);
    }

    @Override
    public void setViews() {
        super.setViews();

        mLayoutProfileHeader.setOnClickListener(this);
        mIvAvatar.placeHolder(R.mipmap.ic_default_user_header)
                .url(mAvatarUrl)
                .renderer(new CircleRenderer())
                .load();

        mProgressBar.incrementProgressBy(mProgressProFile);
        mTvPercent.setText(mProgressProFile + "%");
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
                startActForResult(path);
            }
            break;
            case KCodePhotograph: {
                // 通过照相机拍的图片
                path = mStrPhotoPath;
                startActForResult(path);
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
    private void startActForResult(String path) {
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

        String str = getRelatedItem(RelatedId.address).getText();
        YSLog.d(TAG, "省市 = " + str);

//        mPcd = null;
//        mPcd = new String[Pcd.KMaxCount];
//        String[] addresses = str.split(Pcd.KSplit);
//        for (int i = 0; i < addresses.length; ++i) {
//            mPcd[i] = addresses[i];
//        }
//
//        YSLog.d(TAG, "province = " + mPcd[Pcd.KProvince]);
//        YSLog.d(TAG, "city = " + mPcd[Pcd.KCity]);
//        YSLog.d(TAG, "area = " + mPcd[Pcd.KDistrict]);
//
//        YSLog.d(TAG, "success hospital level = " + getRelateVal(RelatedId.hospital_grade));
//        NetworkReq r = NetFactory.newModifyBuilder()
//                .headImgUrl(mAvatarUrl)
//                .linkman(getRelateVal(RelatedId.name))
//                .hospital(getRelateVal(RelatedId.hospital))
//                .department(getRelateVal(RelatedId.departments))
//                .hospitalLevel(getRelateVal(RelatedId.hospital_grade))
//                .cmeId(getRelateVal(RelatedId.CME_number))
//                .licence(getRelateVal(RelatedId.certification_number))
//                .title(getRelateVal(RelatedId.title))
//                .province(mPcd[Pcd.KProvince])
//                .city(mPcd[Pcd.KCity])
//                .area(mPcd[Pcd.KDistrict])
//                .builder();
//        refresh(RefreshWay.dialog);
//        exeNetworkReq(KReqModifyId, r);
    }

    private String getRelateVal(@RelatedId int relateId) {
        return getRelatedItem(relateId).getVal();
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
            getRelatedItem(RelatedId.address).text(place.toString());
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
