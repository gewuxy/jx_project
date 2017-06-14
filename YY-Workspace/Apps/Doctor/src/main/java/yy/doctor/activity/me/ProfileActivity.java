package yy.doctor.activity.me;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseFormActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.register.HospitalActivity;
import yy.doctor.activity.register.ProvinceActivity;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.BottomDialog.OnDialogItemClickListener;
import yy.doctor.model.Modify;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.config.GlConfig;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.model.me.UpHeadImage;
import yy.doctor.model.me.UpHeadImage.TUpHeadImage;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
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

    private static final String KPhotoFileName = "avatar.jpg";

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
                .name("姓名")
                .text(Profile.inst().getString(linkman))
                .hint("必填")
                .enable(false)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_intent)
                .related(RelatedId.hospital)
                .drawable(R.mipmap.ic_more)
                .name("单位")
                .intent(new Intent(this, HospitalActivity.class))
                .text(Profile.inst().getString(hospital))
                .hint("必填")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_intent)
                .related(RelatedId.departments)
                .drawable(R.mipmap.ic_more)
                .name("科室")
                .intent(new Intent(this, SectionActivity.class))
                .text(Profile.inst().getString(department))
                .hint("必填")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.text_dialog)
                .related(RelatedId.hospital_grade)
                .name("医院级别")
                .text(Profile.inst().getString(hosLevel))
                .data(GlConfig.inst().getHospitalGrade())
                .hint("选填")
                .build());

        addItem(new Builder(FormType.divider_large).build());

        /*addItem(new Builder(FormType.et)
                .related(RelatedId.nickname)
                .name("昵称")
                .text(Profile.homeInst().getString(nickname))
                .hint(R.string.hint_not_fill)
                .build());
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et)
                .related(RelatedId.phone_number)
                .name("手机号")
                .text(Profile.homeInst().getString(mobile))
                .hint(R.string.hint_not_fill)
                .build());
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et)
                .related(RelatedId.email)
                .name("电子邮箱")
                .text(Profile.homeInst().getString(username))
                .hint(R.string.hint_not_fill)
                .build());
        addItem(new Builder(FormType.divider_large).build());*/

        /*addItem(new Builder(FormType.profile_checkbox)
                .related(RelatedId.is_open)
                .build());*/

        addItem(new Builder(FormType.text_intent)
                .related(RelatedId.title)
                .name("职称")
                .intent(new Intent(this, TitleActivity.class))
                .text(Profile.inst().getString(TProfile.title))
                .hint("选填")
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et)
                .related(RelatedId.certification_number)
                .name("职业资格证号")
                .text(Profile.inst().getString(licence))
                .hint("选填")
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et)
                .related(RelatedId.CME_number)
                .name("CME卡号")
                .text(Profile.inst().getString(cmeId))
                .hint("选填")
                .build());

        /*addItem(new Builder(FormType.et)
                .related(RelatedId.position)
                .name("职务")
                .text(Profile.homeInst().getString(place))
                .hint(R.string.hint_not_fill)
                .build());
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.text_dialog)
                .related(RelatedId.sex)
                .name("性别")
                .text(R.string.hint_not_fill)
                .data(GlConfig.homeInst().getSexConfigs())
                .build());
        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.text_dialog)
                .related(RelatedId.education_background)
                .name("学历")
                .text(R.string.hint_not_fill)
                .data(GlConfig.homeInst().getEducationBgConfigs())
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
                .name("所在省市")
                .intent(new Intent(this, ProvinceActivity.class))
                .text(str)
                .hint("必填")
                .build());

        addItem(new Builder(FormType.divider_large)
                .build());
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "我的资料", this);
        bar.addTextViewRight("完成并保存", new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh(RefreshWay.dialog);
                //如果头像修改了就先上传头像，成功后再修改其他资料
                if (mBmp != null) {
                    exeNetworkReq(KReqUpHeaderImgId, NetFactory.upheadimg(BmpUtil.toBytes(mBmp)));
                } else {
                    modify();
                }
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

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        super.onFormViewClick(v, position, related);
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
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
                //LogMgr.d(TAG, "mBmp.getByteCount() = " + mBmp.getByteCount());
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
        String[] strs = str.split(" ");
        for (int i = 0; i < strs.length; i++) {
            String s = strs[i];
            if (i == 0) {
                mStrProvince = s;
            } else if (i == 1) {
                mStrCity = s;
            } else {
                mStrArea = s;
            }
        }
        YSLog.d(TAG, "province = " + mStrProvince);
        YSLog.d(TAG, "city = " + mStrCity);
        YSLog.d(TAG, "area = " + mStrArea);

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
            return JsonParser.ev(r.getText(), Modify.class);
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
            Result<Modify> r = (Result<Modify>) result;
            if (r.isSucceed()) {
                showToast("更新成功");
                //更新本地的数据
                Profile.inst().update(Profile.inst().put(TProfile.hospital, getRelateVal(RelatedId.hospital)));
                Profile.inst().update(Profile.inst().put(TProfile.department, getRelateVal(RelatedId.departments)));
                Profile.inst().update(Profile.inst().put(TProfile.hosLevel, getRelateVal(RelatedId.hospital_grade)));
                Profile.inst().update(Profile.inst().put(TProfile.title, getRelateVal(RelatedId.title)));
                Profile.inst().update(Profile.inst().put(TProfile.licence, getRelateVal(RelatedId.certification_number)));
                Profile.inst().update(Profile.inst().put(TProfile.cmeId, getRelateVal(RelatedId.CME_number)));

                Profile.inst().update(Profile.inst().put(TProfile.province, mStrProvince));
                Profile.inst().update(Profile.inst().put(TProfile.city, mStrCity));
                if (!TextUtil.isEmpty(mStrArea)) {
                    Profile.inst().update(Profile.inst().put(TProfile.zone, mStrArea));
                }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //回收bitmap
        if (mBmp != null && !mBmp.isRecycled()) {
            ClipImageActivity.RecycleBmp();
        }
        if (mCircleBmp != null && !mCircleBmp.isRecycled()) {
            mCircleBmp.recycle();
        }
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public Uri getUri(Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}
