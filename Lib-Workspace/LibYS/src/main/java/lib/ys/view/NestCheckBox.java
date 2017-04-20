package lib.ys.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

/**
 * 支持里面嵌套一个CheckBox的布局
 *
 * @author yuansui
 */
public class NestCheckBox extends LinearLayout {

    private CheckBox mCb;

    public NestCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnHierarchyChangeListener(new HierarchyChangeListener());
    }

    private class HierarchyChangeListener implements OnHierarchyChangeListener {

        @Override
        public void onChildViewAdded(View parent, View child) {
            CheckBox cb = findCheckBox(child);
            if (cb != null) {
                mCb = cb;
                // 要取消Cb本身的点击功能
                mCb.setClickable(false);
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            CheckBox cb = findCheckBox(child);
            if (cb == mCb) {
                mCb = null;
            }
        }
    }

    private CheckBox findCheckBox(View v) {
        if (v instanceof CheckBox) {
            return (CheckBox) v;
        }
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                CheckBox check = findCheckBox(group.getChildAt(i));
                if (check != null) {
                    return check;
                }
            }
        }
        return null;// 没有找到
    }

    public void setChecked(boolean checked) {
        if (mCb != null) {
            mCb.setChecked(checked);
        }
    }

    public boolean isChecked() {
        if (mCb != null) {
            return mCb.isChecked();
        }
        return false;
    }

    public void setOnCheckedChangeListener(final OnCheckedChangeListener listener) {
        if (mCb == null || listener == null) {
            return;
        }

        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isChecked = mCb.isChecked();
                mCb.setChecked(!isChecked);
                listener.onCheckedChanged(mCb, !isChecked);
            }
        });
    }

    public CheckBox getRealCheckBox() {
        return mCb;
    }
}
