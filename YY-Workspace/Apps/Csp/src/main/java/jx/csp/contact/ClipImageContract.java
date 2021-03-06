package jx.csp.contact;

import android.graphics.Bitmap;

import lib.jx.contract.IContract;

/**
 * @auther HuoXuYu
 * @since 2017/10/27
 */

public interface ClipImageContract {
    interface V extends IContract.View {

        void setData();
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 上传头像
         *
         * @param bitmap
         */
        void upLoadAvatar(Bitmap bitmap);
    }
}
