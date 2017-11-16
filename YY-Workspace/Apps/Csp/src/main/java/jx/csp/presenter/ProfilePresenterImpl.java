package jx.csp.presenter;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.ProfileContract;
import lib.ys.util.permission.PermissionResult;
import lib.yy.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class ProfilePresenterImpl extends BasePresenterImpl<ProfileContract.V> implements ProfileContract.P {

    private final int KPermissionCodePhoto = 0;
    private final int KPermissionCodeAlbum = 1;

    public ProfilePresenterImpl(ProfileContract.V v) {
        super(v);
    }

    @Override
    public void checkPhonePermission(int code, @PermissionResult int result) {
        if (code == KPermissionCodePhoto) {
            switch (result) {
                case PermissionResult.granted: {
                    getView().getPhotoFromCamera();
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    App.showToast(R.string.user_photo_permission);
                }
                break;
            }
        } else if (code == KPermissionCodeAlbum) {
            switch (result) {
                case PermissionResult.granted: {
                    getView().getPhotoFromAlbum();
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    App.showToast(R.string.user_album_permission);
                }
                break;
            }
        }
    }
}
