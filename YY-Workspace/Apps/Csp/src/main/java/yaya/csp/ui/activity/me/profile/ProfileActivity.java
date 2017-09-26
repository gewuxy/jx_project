package yaya.csp.ui.activity.me.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.FileSuffix;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yaya.csp.R;
import yaya.csp.dialog.BottomDialog;
import yaya.csp.model.Profile;
import yaya.csp.model.Profile.TProfile;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;
import yaya.csp.model.form.text.IntentForm.IntentType;
import yaya.csp.util.CacheUtil;
import yaya.csp.util.Util;

/**
 * 个人中心
 *
 * @auther Huoxuyu
 * @since 2017/9/21
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

    private NetworkImageView mIvAvatar;
    private RelativeLayout mLayoutHeader;

    private String mStrPhotoPath;
    private Bitmap mBmp;
    private Bitmap mCircleBmp;

    @IntDef({
            RelatedId.name,
            RelatedId.intro,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int name = 0;
        int intro = 1;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.name)
                .layout(R.layout.form_text_nick_name)
                .name(R.string.my_message_nick_name)
                .limit(18)
                .intent(NickNameActivityRouter.newIntent(this, TProfile.user_name))
                .text(Profile.inst().getString(TProfile.user_name))
                .hint("未输入")
                .type(IntentType.name));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.intro)
                .layout(R.layout.form_text_intro)
                .name(R.string.my_message_intro)
                .intent(IntroActivityRouter.newIntent(this, TProfile.info))
                .text(Profile.inst().getString(TProfile.info))
                .type(IntentType.intro)
                .hint(R.string.my_message_person_intro));

    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_profile_header);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.my_message, this);
    }

    @Override
    public void findViews() {
        super.findViews();
        mIvAvatar = findView(R.id.profile_header_iv_avatar);
        mLayoutHeader = findView(R.id.layout_profile_header);
    }

    @Override
    public void setViews() {
        super.setViews();

        mLayoutHeader.setOnClickListener(this);
        mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                .renderer(new CircleRenderer())
                .url(Profile.inst().getString(TProfile.avatar))
                .load();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

        dialog.addItem(getString(R.string.my_message_take_photo), KColorNormal);
        dialog.addItem(getString(R.string.my_message_from_album_select), KColorNormal);
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
    protected void onDestroy() {
        super.onDestroy();
        //回收bitmap
        ClipImageActivity.recycleBmp();
        BmpUtil.recycle(mCircleBmp);
    }
}