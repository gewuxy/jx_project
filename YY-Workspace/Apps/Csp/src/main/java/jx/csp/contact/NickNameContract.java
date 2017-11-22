package jx.csp.contact;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/11/22
 */

public interface NickNameContract {

    interface V extends IContract.View {
        /**
         * 设置文本清除
         *
         * @param et
         */
        void setTextClear(EditText et);

        /**
         * 禁止输入空格
         *
         * @param et
         * @param text
         */
        void inhibitInputBlank(@NonNull EditText et, String text);

    }

    interface P extends IContract.Presenter<V> {

        /**
         * 输入框的监听
         *
         * @param tv
         * @param et
         * @param iv
         */
        void onTextChangedListener(@NonNull TextView tv, @NonNull EditText et, @Nullable View iv);

        /**
         * 输入空格的监听
         *
         * @param et
         */
        void onTextBlankListener(@NonNull EditText et);
    }
}
