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

    // 代表选中状态的集合
    private static final int[] KStateSetChecked = new int[]{
            android.R.attr.state_checked
    };


    private CheckBox mCb;
    private OnCheckedChangeListener mListener;

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

                setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        boolean isChecked = mCb.isChecked();
                        mCb.toggle();

                        refreshBgState();

                        if (mListener != null) {
                            mListener.onCheckedChanged(mCb, !isChecked);
                        }
                    }
                });

                refreshBgState();
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

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }

    /**
     * 改变背景状态
     */
    private void refreshBgState() {
        refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if (mCb != null && !mCb.isChecked()) {
            // 如果未选中，直接返回父类的结果
            return super.onCreateDrawableState(extraSpace);
        } else {
            // 如果选中，将父类的结果和选中状态合并之后返回
            return mergeDrawableStates(super.onCreateDrawableState(extraSpace + 1), KStateSetChecked);
        }
    }

    public CheckBox getRealCheckBox() {
        return mCb;
    }
}
