package jx.csp.contact;

import android.graphics.Bitmap;

import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public interface ClipImageContract {
    interface V extends IContract.View {

        void setProcessResult();
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
