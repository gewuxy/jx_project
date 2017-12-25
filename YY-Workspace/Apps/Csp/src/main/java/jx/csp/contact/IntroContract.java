package jx.csp.contact;

import android.widget.EditText;

import lib.jx.contract.IContract;

/**
 * @auther HuoXuYu
 * @since 2017/11/22
 */

public interface IntroContract {
    interface V extends IContract.View {
        /**
         * 设置简介的文本长度
         *
         * @param length
         */
        void setIntroTextLength(int length);
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 输入框的监听
         *
         * @param et
         */
        void onTextChangedListener(EditText et);
    }
}
