package lib.ys.form;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.View.OnClickListener;

import lib.network.model.NetworkReq;
import lib.ys.AppEx;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormEx.TFormElem;
import lib.ys.model.EVal;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.frag.FragEx;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;

abstract public class FormEx<VH extends ViewHolderEx> extends EVal<TFormElem> implements OnClickListener {

    /**
     * 元素
     */
    public enum TFormElem {
        /**
         * 显示和操作相关的字段
         */
        name, // 名称
        text, // 文本
        text_multi, // 多个文本
        hint, // 暗示
        tips, // 明示
        enable, // 是否可编辑
        data, // 计算显示等需要的数据
        option, // 点击操作等需要的数据
        host,// 宿主activity/fragment
        related, // 关联的hash map类型
        limit, // 数量限制, 如输入字数, 图片最大数量等
        drawable, // 图片id或url
        background, // 背景色
        width, // 宽
        height, // 高
        column, // 列数
        mode, // 模式
        layout, // 布局resId
        toast, // toast提示
        intent, // 意图
        children, // FormItem类型的子列表
        depend, // 依赖(做操作的时候需要的前提条件)
        regex, // 正则的验证规则
        check, // 适合checkbox等的规则
        index, // 下标
        id, // 标识位
        visible,// 显示与否
        observer, // 观察者, 某些数据的相应回调
        text_color, // 字体颜色

        /**
         * 和服务器通信的主要字段
         */
        key, // key
        val, // value
    }

    @NonNull
    abstract public int getType();

    private int mPosition;
    private OnFormViewClickListener mListener;
    private VH mHolder;

    public void setAttrs(VH holder, int position, OnFormViewClickListener listener) {
        mPosition = position;
        mListener = listener;
        mHolder = holder;

        init(holder);
        refresh();
    }

    public int getPosition() {
        return mPosition;
    }

    /**
     * 刷新数据
     *
     * @param holder
     */
    abstract protected void refresh(VH holder);

    /**
     * 初始化设定
     *
     * @param holder
     */
    protected void init(VH holder) {
    }

    public final void refresh() {
        if (mHolder != null) {
            if (getBoolean(TFormElem.visible)) {
                ViewUtil.showView(mHolder.getConvertView());
                refresh(mHolder);
            } else {
                ViewUtil.goneView(mHolder.getConvertView());
            }
        }
    }

    public final void show() {
        put(TFormElem.visible, true);
        refresh();
    }

    public final void hide() {
        put(TFormElem.visible, false);
        refresh();
    }

    public VH getHolder() {
        return mHolder;
    }

    abstract public boolean check();

    public boolean onItemClick(Object host, View v) {
        return false;
    }

    /**
     * 响应Activity的返回消息
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, data);
    }

    protected void onActivityResult(int position, Intent data) {
        // 部分item无此消息响应
    }

    protected void startActivity(Intent intent) {
        Object host = getObject(TFormElem.host);
        LaunchUtil.startActivity(host, intent);
    }

    protected void startActivityForResult(Intent intent, int position) {
        Object host = getObject(TFormElem.host);
        LaunchUtil.startActivityForResult(host, intent, position);
    }

    protected void startActivityForResult(Class<?> clz, int position) {
        startActivityForResult(new Intent(getContext(), clz), position);
    }

    @Override
    public final void onClick(View v) {
        if (!onViewClick(v)) {
            if (mListener != null) {
                mListener.onViewClick(v, mPosition, getObject(TFormElem.related));
            }
        }
    }

    /**
     * 内部处理view的点击事件
     *
     * @param v
     * @return true的话表示处理了, false表示外部处理
     */
    protected boolean onViewClick(View v) {
        return false;
    }

    protected void setOnClickListener(@NonNull View v) {
        if (v == null) {
            YSLog.d(TAG, "setOnClickListener()" + "v is null");
            return;
        }
        v.setOnClickListener(this);
    }

    protected void removeOnClickListener(@NonNull View v) {
        if (v == null) {
            YSLog.d(TAG, "removeOnClickListener()" + "v is null");
            return;
        }
        v.setClickable(false);
        v.setOnClickListener(null);
    }

    @LayoutRes
    abstract public int getContentViewResId();

    protected Context getContext() {
        return AppEx.getContext();
    }

    public void showToast(String content) {
        AppEx.showToast(content);
    }

    public void showToast(@StringRes int resId) {
        AppEx.showToast(resId);
    }

    public void save(Object key, Object value, Object text) {
        put(TFormElem.key, key);
        put(TFormElem.val, value);
        put(TFormElem.text, text);
    }

    public void save(Object value, Object text) {
        put(TFormElem.val, value);
        put(TFormElem.text, text);
    }

    /**
     * 重设之前的值
     */
    public void reset() {
        save(ConstantsEx.KEmptyValue, ConstantsEx.KEmptyValue, ConstantsEx.KEmptyValue);
    }

    /**
     * 在host层执行网络任务
     *
     * @param networkId
     * @param request
     */
    protected void exeNetworkRequest(int networkId, NetworkReq request) {
        Object host = getObject(TFormElem.host);
        if (host instanceof ActivityEx) {
            ActivityEx act = (ActivityEx) host;
            act.refresh(RefreshWay.dialog);
            act.exeNetworkReq(networkId, request);
        } else if (host instanceof FragEx) {
            FragEx frag = (FragEx) host;
            frag.refresh(RefreshWay.dialog);
            frag.exeNetworkReq(networkId, request);
        }
    }

    protected boolean isEmpty(CharSequence text) {
        return TextUtil.isEmpty(text);
    }
}
