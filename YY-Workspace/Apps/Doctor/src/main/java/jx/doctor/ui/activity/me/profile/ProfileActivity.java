package jx.doctor.ui.activity.me.profile;

import android.animation.ObjectAnimator;
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

import lib.ys.YSLog;
import lib.ys.form.OnFormObserver;
import lib.ys.model.FileSuffix;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.shape.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.AnimateUtil;
import lib.ys.util.PhotoUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.jx.model.form.BaseForm;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseFormActivity;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.dialog.BottomDialog;
import jx.doctor.model.Place;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.form.Form;
import jx.doctor.model.form.FormType;
import jx.doctor.model.form.text.intent.IntentForm.IntentType;
import jx.doctor.model.hospital.HospitalLevel;
import jx.doctor.model.hospital.HospitalLevel.THospitalLevel;
import jx.doctor.model.hospital.HospitalName;
import jx.doctor.model.hospital.HospitalName.THospitalName;
import jx.doctor.ui.activity.user.PcdActivity;
import jx.doctor.ui.activity.user.hospital.BaseHospitalActivity.FromType;
import jx.doctor.ui.activity.user.hospital.HospitalActivity;
import jx.doctor.util.CacheUtil;
import jx.doctor.util.Util;

import static jx.doctor.model.Profile.TProfile.category;
import static jx.doctor.model.Profile.TProfile.name;

/**
 * 我的资料
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class ProfileActivity extends BaseFormActivity implements OnFormObserver {

    private final int KColorNormal = Color.parseColor("#666666");
    private final int KColorCancel = Color.parseColor("#01b557");
    private final long KAnimatorTime = 2000;

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
        int cme_number = 3;
        int certification_number = 4;
        int title = 5;
        int address = 6;
        int specialized = 7;
        int skill = 8;
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
        addItem(Form.create(FormType.text)
                .related(RelatedId.name)
                .observer(this)
                .name(R.string.user_name)
                .text(Profile.inst().getString(TProfile.linkman))
                .hint(R.string.required)
                .enable(false));

        addItem(Form.create(FormType.divider));
        HospitalLevel p = Profile.inst().get(TProfile.systemProperties);
        String url = "";
        if (p != null) {
            url = p.getString(THospitalLevel.picture);
            YSLog.d(TAG, "initData:" + url);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.hospital)
                .observer(this)
                .layout(R.layout.form_text_hospital)
                .name(R.string.user_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, FromType.profile))
                .type(IntentType.hospital)
                .text(Profile.inst().getString(TProfile.hospital))
                .url(url)
                .hint(R.string.choose_hospital));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.departments)
                .observer(this)
                .name(R.string.user_section)
                .limit(24)
                .intent(ModifyTextActivityRouter.newIntent(this, TProfile.department, R.string.user_section, R.string.user_input_section))
                .type(IntentType.section)
                .text(Profile.inst().getString(TProfile.department))
                .hint(R.string.user_input_section));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.address)
                .observer(this)
                .name(R.string.user_city)
                .intent(new Intent(this, PcdActivity.class))
                .type(IntentType.location)
                .text(Profile.inst().getPlace().getDesc())
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
                .intent(ModifyTextActivityRouter.newIntent(this, TProfile.cmeId, R.string.user_CME_number, R.string.user_input_CME_number))
                .type(IntentType.cme_number)
                .text(Profile.inst().getString(TProfile.cmeId))
                .hint(R.string.user_input_CME_number));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.certification_number)
                .observer(this)
                .name(R.string.user_certification_number)
                .limit(30)
                .intent(ModifyTextActivityRouter.newIntent(this, TProfile.licence, R.string.user_certification_number, R.string.user_input_certification_number))
                .type(IntentType.certification)
                .text(Profile.inst().getString(TProfile.licence))
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
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.skill)
                .layout(R.layout.form_text_academic)
                .observer(this)
                .name(R.string.medical_skill)
                .intent(SkillActivityRouter.newIntent(this, TProfile.major, R.string.medical_skill, R.string.user_input_academic))
                .type(IntentType.skill)
                .hint(R.string.user_input_academic)
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
        mRlHeader = findView(R.id.profile_layout_header_integrity);
    }

    @Override
    public void setViews() {
        super.setViews();

        mLayoutProfileHeader.setOnClickListener(this);
        mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                .url(mAvatarUrl)
                .renderer(new CircleRenderer())
                .load();

        for (int i = 0; i < getCount(); ++i) {
            BaseForm item = getItem(i);
            if (TextUtil.isNotEmpty(item.getText()) || TextUtil.isNotEmpty(item.getVal())) {
                // 头像是使用的是val
                mStatus.add(item.getRelated());
            }
        }

        setProgress();
    }

    @Override
    public void callback(Object... params) {
        int related = (int) params[0];
        boolean valid = (boolean) params[1];

        if (valid) {
            if (!mStatus.contains(related)) {
                mStatus.add(related);
            }
        } else {
            mStatus.remove(related);
        }

        int diff = mStatus.size() * 10 - mProgressProFile;
        mProgressBar.incrementProgressBy(diff);

        setProgress();
    }

    private void setProgress() {
        mProgressBar.setProgress(0);
        mProgressProFile = mStatus.size() * 10;
        Profile.inst().put(TProfile.integrity, mProgressProFile);
        Profile.inst().saveToSp();
        if (mProgressProFile == 100) {
            goneView(mRlHeader);
        } else {
            showView(mRlHeader);
            mTvPercent.setText(mProgressProFile + "%");

            //属性动画  添加了背景颜色
            int width = getWindowManager().getDefaultDisplay().getWidth();
            float process = width * mProgressProFile / 100.0f;
            ObjectAnimator animator = AnimateUtil.ofFloatX(mProgressBar, 0f, process, KAnimatorTime);
            animator.start();
        }
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
                    if (checkPermission(KPermissionCodePhoto, Permission.camera)) {
                        getPhotoFromCamera();
                    }
                }
                break;
                case 1: {
                    if (checkPermission(KPermissionCodeAlbum, Permission.storage)) {
                        getPhotoFromAlbum();
                    }
                }
                break;
            }
        });

        dialog.addItem(getString(R.string.take_photo), KColorNormal);
        dialog.addItem(getString(R.string.from_album_select), KColorNormal);
        dialog.addItem(getString(R.string.cancel), KColorCancel);

        dialog.show();
    }

    private void getPhotoFromAlbum() {
        PhotoUtil.fromAlbum(this, KCodeAlbum);
    }

    private void getPhotoFromCamera() {
        mStrPhotoPath = CacheUtil.getUploadCacheDir() + KPhotoCameraPrefix + System.currentTimeMillis() + FileSuffix.jpg;
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

                notify(NotifyType.profile_change);

                mStatus.add(KAvatarCheckStatus);
                setProgress();
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
            ClipImageActivityRouter.create(path).route(this, KCodeClipImage);
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

        if (type == NotifyType.pcd_selected) {
            Place place = (Place) data;
            String text = place.getDesc();
            getRelatedItem(RelatedId.address).save(text, text);
            refreshRelatedItem(RelatedId.address);
        } else if (type == NotifyType.hospital_finish) {
            if (data instanceof HospitalName) {
                HospitalName h = (HospitalName) data;
                String hospital = h.getString(THospitalName.name);
                getRelatedItem(RelatedId.hospital).save(hospital, hospital);
                getRelatedItem(RelatedId.hospital).url(h.get(THospitalName.level).getString(THospitalLevel.picture));
            }
            refreshRelatedItem(RelatedId.hospital);
            notify(NotifyType.profile_change); // 引起我的界面的刷新
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
