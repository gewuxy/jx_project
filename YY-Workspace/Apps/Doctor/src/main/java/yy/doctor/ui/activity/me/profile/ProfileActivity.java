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
import java.util.HashSet;
import java.util.Set;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.ConstantsEx.FileSuffix;
import lib.ys.form.OnFormObserver;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.model.form.BaseForm;
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
import yy.doctor.ui.activity.register.HospitalActivity;
import yy.doctor.ui.activity.register.ProvinceActivity;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

import static yy.doctor.model.Profile.TProfile.category;
import static yy.doctor.model.Profile.TProfile.cmeId;
import static yy.doctor.model.Profile.TProfile.department;
import static yy.doctor.model.Profile.TProfile.hosLevel;
import static yy.doctor.model.Profile.TProfile.hospital;
import static yy.doctor.model.Profile.TProfile.licence;
import static yy.doctor.model.Profile.TProfile.linkman;
import static yy.doctor.model.Profile.TProfile.major;
import static yy.doctor.model.Profile.TProfile.name;

/**
 * 我的资料
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class ProfileActivity extends BaseFormActivity implements OnFormObserver {

    private final int KColorNormal = Color.parseColor("#666666");
    private final int KColorCancel = Color.parseColor("#01b557");

    private final int KBaseCode = 1000;
    private final int KCodeAlbum = KBaseCode + 1;
    private final int KCodePhotograph = KBaseCode + 2;
    private final int KCodeClipImage = KBaseCode + 3;

    private final int KAvatarCheckStatus = 1000;

    private final int KPermissionCodePhoto = 0;
    private final int KPermissionCodeAlbum = 1;

    private final String KPhotoCameraPrefix = "avatar";

    private RelativeLayout mLayoutProfileHeader;
    private NetworkImageView mIvAvatar;

    private Bitmap mBmp;
    private Bitmap mCircleBmp;
    private String mStrPhotoPath;
    private String mAvatarUrl;

    private RelativeLayout mRlHeader;
    private ProgressBar mProgressBar;
    private TextView mTvPercent;
    private int mProgressProFile = 0;

    private Set<Integer> mStatus;

    @IntDef({
            RelatedId.name,
            RelatedId.hospital,
            RelatedId.departments,
            RelatedId.phone_number,
            RelatedId.cme_number,
            RelatedId.certification_number,
            RelatedId.title,
            RelatedId.address,
            RelatedId.specialized,
            RelatedId.skill,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int name = 0;
        int hospital = 1;
        int departments = 2;
        int phone_number = 3;
        int cme_number = 4;
        int certification_number = 5;
        int title = 6;
        int address = 7;
        int specialized = 8;
        int skill = 9;
    }

    @Override
    public void initData() {
        super.initData();

        mStatus = new HashSet<>();
        mAvatarUrl = Profile.inst().getString(TProfile.headimg);
        if (TextUtil.isNotEmpty(mAvatarUrl)) {
            mStatus.add(KAvatarCheckStatus);
        }

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et)
                .related(RelatedId.name)
                .observer(this)
                .name(R.string.user_name)
                .text(Profile.inst().getString(linkman))
                .hint(R.string.required)
                .enable(false));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent_hospital)
                .related(RelatedId.hospital)
                .observer(this)
                .name(R.string.user_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, IntentType.hospital))
                .type(IntentType.hospital)
                .text(Profile.inst().getString(hospital))
                .drawable(Profile.inst().getInt(hosLevel))
                .hint(R.string.choose_hospital));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.departments)
                .observer(this)
                .name(R.string.user_section)
                .limit(24)
                .intent(ModifyTextActivity.newIntent(this, getString(R.string.user_section), department))
                .type(IntentType.section)
                .text(Profile.inst().getString(department))
                .hint(R.string.user_input_section));

        // FIXME: 是否默认显示定位城市
        Place place = new Place();
        place.put(TPlace.province, Profile.inst().getString(TProfile.province));
        place.put(TPlace.city, Profile.inst().getString(TProfile.city));
        place.put(TPlace.district, Profile.inst().getString(TProfile.zone));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.address)
                .observer(this)
                .name(R.string.user_city)
                .intent(new Intent(this, ProvinceActivity.class).putExtra(Extra.KData, IntentType.location))
                .type(IntentType.location)
                .text(place.toString())
                .hint(R.string.province_city_district));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.specialized)
                .observer(this)
                .intent(new Intent(this, SectionActivity.class))
                .type(IntentType.medicine)
                .name(R.string.specialized)
                .text(Profile.inst().getString(category) + " " + Profile.inst().getString(name))
                .hint(R.string.user_input_Specialist));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.cme_number)
                .observer(this)
                .name(R.string.user_CME_number)
                .limit(30)
                .intent(ModifyTextActivity.newIntent(this, getString(R.string.user_CME_number), cmeId))
                .type(IntentType.cme_number)
                .text(Profile.inst().getString(cmeId))
                .hint(R.string.user_input_CME_number));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.certification_number)
                .observer(this)
                .name(R.string.user_certification_number)
                .limit(30)
                .intent(ModifyTextActivity.newIntent(this, getString(R.string.user_certification_number), licence))
                .type(IntentType.certification)
                .text(Profile.inst().getString(licence))
                .hint(R.string.user_input_certification_number));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.title)
                .observer(this)
                .name(R.string.user_title)
                .intent(new Intent(this, TitleActivity.class))
                .type(IntentType.doctor_title)
                .text(Profile.inst().getString(TProfile.title))
                .hint(R.string.user_title));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.modify_intent_skill)
                .related(RelatedId.skill)
                .observer(this)
                .name(R.string.medical_skill)
                .intent(SkillActivity.newIntent(this, getString(R.string.medical_skill), major))
                .type(IntentType.skill)
                .text(Profile.inst().getString(TProfile.major)));
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
        mProgressBar = findView(R.id.profile_pb_progress_bar);
        mTvPercent = findView(R.id.profile_tv_percent);
        mRlHeader = findView(R.id.profile_rl_header);
    }

    @Override
    public void setViews() {
        super.setViews();

        mLayoutProfileHeader.setOnClickListener(this);
        mIvAvatar.placeHolder(R.mipmap.ic_default_user_header)
                .url(mAvatarUrl)
                .renderer(new CircleRenderer())
                .load();

        for (int i = 0; i < getCount(); ++i) {
            BaseForm item = getItem(i);
            if (TextUtil.isNotEmpty(item.getText())
                    || TextUtil.isNotEmpty(item.getVal())) { // 头像是使用的是val
                mStatus.add(i);
            }
        }

        setProgress();
    }

    @Override
    public void callback(Object... params) {
        int position = (int) params[0];
        boolean valid = (boolean) params[1];

        if (valid) {
            if (!mStatus.contains(position)) {
                mStatus.add(position);
            }
        } else {
            mStatus.remove(position);
        }

        int diff = mStatus.size() * 10 - mProgressProFile;
        mProgressBar.incrementProgressBy(diff);

        setProgress();
    }

    private void setProgress() {
        mProgressProFile = mStatus.size() * 10;
        mProgressBar.setProgress(mProgressProFile);
        mTvPercent.setText(mProgressProFile + "%");
        if (mProgressProFile == 100) {
            mRlHeader.setVisibility(View.GONE);
        } else {
            mRlHeader.setVisibility(View.VISIBLE);
        }
        Profile.inst().put(TProfile.integrity, mProgressProFile);
        Profile.inst().saveToSp();
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
        mStrPhotoPath = CacheUtil.getUploadCacheDir() + KPhotoCameraPrefix + System.currentTimeMillis() + FileSuffix.KJpg;
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
            case KCodeClipImage: {
                mBmp = ClipImageActivity.mBmp;
                mCircleBmp = BmpUtil.toCircle(mBmp);
                mIvAvatar.setImageBitmap(mCircleBmp);
                //YSLog.d(TAG, "mBmp.getByteCount() = " + mBmp.getByteCount());

                mStatus.add(KAvatarCheckStatus);
                notify(NotifyType.profile_change);
            }
            break;
            default: {
                // 其他的操作都是來自form
                notify(NotifyType.profile_change);
            }
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
            String text = place.toString();
            getRelatedItem(RelatedId.address).save(text, text);
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
