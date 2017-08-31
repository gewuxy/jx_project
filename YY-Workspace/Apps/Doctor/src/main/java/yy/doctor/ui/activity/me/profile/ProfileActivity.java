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
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import lib.ys.YSLog;
import lib.ys.form.OnFormObserver;
import lib.ys.model.FileSuffix;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.timeTick.TimeInterpolator;
import lib.ys.timeTick.TimeInterpolator.InterpolatorType;
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
import yy.doctor.model.Place;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.text.intent.IntentForm.IntentType;
import yy.doctor.model.hospital.HospitalLevel;
import yy.doctor.model.hospital.HospitalLevel.THospitalLevel;
import yy.doctor.ui.activity.user.PcdActivity;
import yy.doctor.ui.activity.user.hospital.HospitalActivity;
import yy.doctor.ui.activity.user.hospital.SearchHospitalActivity.Hos;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

import static yy.doctor.model.Profile.TProfile.category;
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
        HospitalLevel p = Profile.inst().getEv(TProfile.systemProperties);
        String string = "";
        if (p != null) {
            string = p.getString(THospitalLevel.picture);
            YSLog.d(TAG, "initData:" + string);
        }
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.hospital)
                .observer(this)
                .layout(R.layout.form_text_hospital)
                .name(R.string.user_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, IntentType.hospital))
                .type(IntentType.hospital)
                .text(Profile.inst().getString(TProfile.hospital))
                .url(string)
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
        mIvAvatar.placeHolder(R.mipmap.ic_default_user_header)
                .url(mAvatarUrl)
                .renderer(new CircleRenderer())
                .load();

        for (int i = 0; i < getCount(); ++i) {
            BaseForm item = getItem(i);
            if (TextUtil.isNotEmpty(item.getText())
                    || TextUtil.isNotEmpty(item.getVal())) { // 头像是使用的是val
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
        mProgressProFile = mStatus.size() * 10;

//        mProgressBar.setProgress(mProgressProFile);

        TimeInterpolator interpolator = TimeInterpolator.create(InterpolatorType.accelerate);
        interpolator.start(TimeUnit.SECONDS.toMillis(2));
        Flowable.interval(0, 60, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    float i = interpolator.getInterpolation();
                   int progress =  (int) (interpolator.getInterpolation() * mProgressProFile);
                    mProgressBar.setProgress(progress);
                    if (i == 1) {
                        // stop
                    }
                });
        
        
//        Flowable.intervalRange(0, mProgressProFile, 0, 60, TimeUnit.MILLISECONDS)
//                .map(aLong -> )
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(aLong -> mProgressBar.setProgress(Long.valueOf(aLong).intValue()));


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
        } else if(type == NotifyType.hospital_finish) {
            if (data instanceof Hos) {
                Hos level = (Hos) data;
                String hospital = level.mName;
                String url = level.mHospitalLevel.getString(THospitalLevel.picture);
                YSLog.d("asdad", "onActivityResult:" + url);
                getRelatedItem(RelatedId.hospital).save(hospital, hospital);
                getRelatedItem(RelatedId.hospital).url(level.mHospitalLevel.getString(THospitalLevel.picture));
            }
            refreshRelatedItem(RelatedId.hospital);
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
