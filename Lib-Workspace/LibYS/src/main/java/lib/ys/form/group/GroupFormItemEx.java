package lib.ys.form.group;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.form.FormItemEx;

/**
 * TODO 未改完
 *
 * @param <VH>
 */
abstract public class GroupFormItemEx<VH extends ViewHolderEx> extends FormItemEx<VH> {

    private List<ChildFormItemEx> mChildItems;

    public void setChildItems(List<ChildFormItemEx> items) {
        if (mChildItems != null) {
            mChildItems.clear();
        }
        mChildItems = items;
    }

    public List<ChildFormItemEx> getChildItems() {
        return mChildItems;
    }

    protected void refreshChildItems() {
        for (int i = 0; i < mChildItems.size(); i++) {
            mChildItems.get(i).refresh();
        }
    }

    protected void refreshChildItem(int position) {
        mChildItems.get(position).refresh();
    }

    /**
     * 通用startActivityForResult方法，主要为了获取requestCode刷新相应条目
     *
     * @param obj
     * @param intent
     * @param isGroup
     * @param groupPosition
     */
    protected void startActivityForResult(Object obj, Intent intent, boolean isGroup, int groupPosition) {
        int i = isGroup ? 1 : 0;
        int requestCode = (groupPosition << ConstantsEx.KGroupOffset) + i;

        if (obj instanceof Activity) {
            ((Activity) obj).startActivityForResult(intent, requestCode);
        } else if (obj instanceof Fragment) {
            ((Fragment) obj).startActivityForResult(intent, requestCode);
        }
    }
}
