package jx.csp.ui.activity.me.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.contact.ProfileContract;
import jx.csp.dialog.BottomDialog;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.model.form.text.IntentForm.IntentType;
import jx.csp.presenter.ProfilePresenterImpl;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
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

/**
 * 个人中心
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */

public class ProfileActivity extends BaseFormActivity {

    private final int KColorNormal = R.color.text_666;
    private final int KColorCancel = R.color.text_01b557;

    private final int KCodeAlbum = 1001;
    private final int KCodePhotograph = 1002;
    private final int KCodeClipImage = 1003;

    private final int KPermissionCodePhoto = 0;
    private final int KPermissionCodeAlbum = 1;

    private final String KPhotoCameraPrefix = "avatar";

    private NetworkImageView mIvAvatar;
    private RelativeLayout mLayoutHeader;

    private String mStrPhotoPath;
    private Bitmap mBmp;
    private Bitmap mCircleBmp;

    private ProfileContract.P mPresenter;
    private ProfileContract.V mView;

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

        mView = new ProfileViewImpl();
        mPresenter = new ProfilePresenterImpl(mView);

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.name)
                .layout(R.layout.form_text_nick_name)
                .name(R.string.my_message_nick_name)
                .limit(18)
                .intent(NickNameActivityRouter.newIntent(this, TProfile.nickName, false))
                .text(Profile.inst().getString(TProfile.nickName))
                .hint(R.string.my_message_no_input)
                .type(IntentType.name));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.intro)
                .layout(R.layout.form_text_intro)
                .name(R.string.my_message_intro)
                .intent(IntroActivityRouter.newIntent(this, TProfile.info, true))
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
        mView.setAvatar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_profile_header: {
                mView.showDialogSelectPhoto();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mView.getActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        mView.getPermissionResult(code, result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mView.onDestroy();
    }

    private class ProfileViewImpl implements ProfileContract.V {
        @Override
        public void setAvatar() {
            mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                    .renderer(new CircleRenderer())
                    .url(Profile.inst().getString(TProfile.avatar))
                    .load();
        }

        @Override
        public void showDialogSelectPhoto() {
            final BottomDialog dialog = new BottomDialog(ProfileActivity.this, position -> {

                switch (position) {
                    case KPermissionCodePhoto: {
                        if (checkPermission(KPermissionCodePhoto, Permission.camera)) {
                            getPhotoFromCamera();
                        }
                    }
                    break;
                    case KPermissionCodeAlbum: {
                        if (checkPermission(KPermissionCodeAlbum, Permission.storage)) {
                            getPhotoFromAlbum();
                        }
                    }
                    break;
                }
            });

            dialog.addItem(getString(R.string.my_message_take_photo), ResLoader.getColor(KColorNormal));
            dialog.addItem(getString(R.string.my_message_from_album_select), ResLoader.getColor(KColorNormal));
            dialog.addItem(getString(R.string.cancel), ResLoader.getColor(KColorCancel));
            dialog.show();
        }

        @Override
        public void getPhotoFromAlbum() {
            PhotoUtil.fromAlbum(ProfileActivity.this, KCodeAlbum);

        }

        @Override
        public void getPhotoFromCamera() {
            mStrPhotoPath = CacheUtil.getUploadCacheDir() + KPhotoCameraPrefix + System.currentTimeMillis() + FileSuffix.jpg;
            PhotoUtil.fromCamera(ProfileActivity.this, mStrPhotoPath, KCodePhotograph);
        }

        @Override
        public void getPermissionResult(int code, @PermissionResult int result) {
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
        public void getActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode != RESULT_OK) {
                return;
            }

            switch (requestCode) {
                case KCodeAlbum: {
                    // 查看相册获得图片返回
                    String path = PhotoUtil.getPath(ProfileActivity.this, data.getData());
                    startActForResult(path);
                }
                break;
                case KCodePhotograph: {
                    // 通过照相机拍的图片
                    startActForResult(mStrPhotoPath);
                }
                break;
                case KCodeClipImage: {
                    mBmp = ClipImageActivity.mBmp;
                    mCircleBmp = BmpUtil.toCircle(mBmp);
                    mIvAvatar.setImageBitmap(mCircleBmp);

                    ProfileActivity.this.notify(NotifyType.profile_change);
                }
                break;
                default: {
                    // 其他的操作都是來自form
                    ProfileActivity.this.notify(NotifyType.profile_change);
                }
            }
        }

        @Override
        public void startActForResult(String path) {
            if (path != null) {
                ClipImageActivityRouter.create(path).route(ProfileActivity.this, KCodeClipImage);
            }
        }

        @Override
        public void onDestroy() {
            //回收bitmap
            ClipImageActivity.recycleBmp();
            BmpUtil.recycle(mCircleBmp);
        }
    }
}
