package jx.csp.contact;

import android.content.Intent;

import jx.csp.ui.ViewEx;
import lib.ys.util.permission.PermissionResult;
import lib.yy.contract.BaseContract;
import lib.yy.contract.BaseContract.BaseView;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public interface ProfileContract {
    interface V extends ViewEx, BaseView {

        /**
         * 初始化头像
         */
        void setAvatar();

        /**
         * 点击展示dialog, 选择拍照或相册
         */
        void showDialogSelectPhoto();

        void getPhotoFromAlbum();

        void getPhotoFromCamera();

        void getPermissionResult(int code, @PermissionResult int result);

        /**
         * 处理获取的图片结果
         *
         * @param requestCode
         * @param resultCode
         * @param data
         */
        void getActivityResult(int requestCode, int resultCode, Intent data);

        /**
         * 获取图片, 进行裁剪后返回
         * @param path
         */
        void startActForResult(String path);

        /**
         * 回收bitmap
         */
        void onDestroy();

    }

    interface P extends BaseContract.BasePresenter{

    }
}
