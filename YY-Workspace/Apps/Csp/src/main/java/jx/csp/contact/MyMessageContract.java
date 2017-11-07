package jx.csp.contact;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public interface MyMessageContract {
    interface V extends IContract.View {

        /**
         * 设置头部
         * @param bar
         */
        void setNavBar(NavBar bar);
        void setNavBarTextColor();

        /**
         * 设置头部文本状态
         */
        void setTextButtonStatus();

        /**
         * 保存数据
         */
        void saveRevisedData();

        /**
         * 获取输入框数据
         */
        void getEtData();

        /**
         * 基类输入框的监听
         * @param et
         * @param ivClear
         */
        void onTextChangedListener(@NonNull EditText et, @Nullable android.view.View ivClear);

        /**
         * 昵称输入框的规则
         * @param s
         * @param et
         * @param watcher
         */
        void setNickNameTextListener(Editable s, EditText et, TextWatcher watcher);

        /**
         * 设置昵称的清除
         * @param et
         */
        void setClear(EditText et);

        /**
         * 设置简介的文本长度
         * @param length
         */
        void setIntroTextLength(int length, TextView tv);

        /**
         * 改变简介文本的长度
         * @param s
         */
        void setIntroChangedTextLength(Editable s, TextView tv);
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 保存个人信息
         * @param id
         * @param text
         */
        void savePersonMessage(int id, String text);
    }
}
