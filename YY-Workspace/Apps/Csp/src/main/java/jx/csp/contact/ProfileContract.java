package jx.csp.contact;

import android.content.Intent;

import lib.ys.util.permission.PermissionResult;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public interface ProfileContract {
    interface V extends IContract.View {

        /**
         * 初始化头像
         */
        void setAvatar();

        /**
         * 点击展示dialog, 选择拍照或相册
         */
        void showDialogSelectPhoto();

        /**
         * 查看手机相册
         */
        void getPhotoFromAlbum();

        /**
         * 获取手机拍照
         */
        void getPhotoFromCamera();

        /**
         * 处理获取的图片
         *
         * @param requestCode
         * @param resultCode
         * @param data
         */
        void getActivityResult(int requestCode, int resultCode, Intent data);

        /**
         * 获取图片, 进行裁剪后返回
         *
         * @param path
         */
        void startActForResult(String path);

        /**
         * 回收bitmap
         */
        void onDestroy();

    }

    interface P extends IContract.Presenter<V> {
        /**
         * 检查手机的权限
         *
         * @param code
         * @param result
         */
        void checkPhonePermission(int code, @PermissionResult int result);
    }
}
