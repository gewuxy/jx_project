package jx.csp.contact;

import android.widget.EditText;
import android.widget.TextView;

import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/11/22
 */

public interface IntroContract {
    interface V extends IContract.View{
        /**
         * 设置简介的文本长度
         *
         * @param length
         */
        void setIntroTextLength(int length, TextView tv);
    }

    interface P extends IContract.Presenter<V>{

        /**
         * 输入框的监听
         *
         * @param et
         * @param tv
         */
        void onTextChangedListener(EditText et, TextView tv);
    }
}
