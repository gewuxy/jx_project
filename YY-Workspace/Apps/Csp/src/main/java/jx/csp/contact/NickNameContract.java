package jx.csp.contact;

import android.widget.EditText;

import io.reactivex.annotations.NonNull;
import lib.jx.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/11/22
 */

public interface NickNameContract {

    interface V extends IContract.View {
        /**
         * 设置文本清除
         */
        void setTextClear();

        /**
         * 禁止输入空格
         *
         * @param text
         */
        void forbidInputBlank(String text);

        /**
         * 按钮的状态
         */
        void buttonStatus();

        /**
         * 清除按钮
         *
         * @param text
         */
        void setClearButton(String text);

    }

    interface P extends IContract.Presenter<V> {

        /**
         * 输入框的监听
         *
         * @param et
         */
        void onTextChangedListener(@NonNull EditText et);

        /**
         * 输入空格的监听
         *
         * @param et
         */
        void onTextBlankListener(@NonNull EditText et);
    }
}
