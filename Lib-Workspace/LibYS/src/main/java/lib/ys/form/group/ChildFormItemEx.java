package lib.ys.form.group;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

import lib.ys.ConstantsEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.form.FormItemEx;

/**
 * TODO 未改完
 *
 * @param <VH>
 */
abstract public class ChildFormItemEx<VH extends ViewHolderEx> extends FormItemEx<VH> implements OnClickListener {

    private int mGroupPosition;
    private int mChildPosition;
    private OnGroupFormViewClickListener mListener;

    public void setAttrs(VH holder, int groupPosition, int childPosition, OnGroupFormViewClickListener listener) {
        super.setAttrs(holder, childPosition, null);

        mGroupPosition = groupPosition;
        mChildPosition = childPosition;
        mListener = listener;
    }

    public int getGroupPosition() {
        return mGroupPosition;
    }

    public int getChildPosition() {
        return mChildPosition;
    }

    public void onChildItemClick(Activity act) {
        // 部分item无此消息响应
    }

    public void onChildItemClick(Fragment frag) {
        // 部分item无此消息响应
    }

    /**
     * 局限性：	只能适应group和child条目小于2^8个的情况
     * 通用startActivityForResult方法，主要为了获取requestCode刷新相应条目
     *
     * @param fragment
     * @param intent
     * @param isGroup
     * @param groupPosition
     * @param childPosition
     */
    protected void startActivityForResult(Fragment fragment, Intent intent, boolean isGroup, int groupPosition, int childPosition) {
        int i = isGroup ? 1 : 0;
        int requestCode = (groupPosition << ConstantsEx.KGroupOffset) + (childPosition << ConstantsEx.KChildOffset) + i;
        fragment.startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Activity activity, Intent intent, boolean isGroup, int groupPosition, int childPosition) {
        int i = isGroup ? 1 : 0;
        int requestCode = (groupPosition << ConstantsEx.KGroupOffset) + (childPosition << ConstantsEx.KChildOffset) + i;
        activity.startActivityForResult(intent, requestCode);
    }
}
