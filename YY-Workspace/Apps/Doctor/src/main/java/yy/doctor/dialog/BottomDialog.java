package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import lib.ys.adapter.AdapterEx;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * 在底部的dialog
 *
 * @author CaiXiang
 * @since 2017/4/19
 */
public class BottomDialog extends BaseDialog implements OnItemClickListener {

    private ListView mLv;
    private OnDialogItemClickListener mListener;
    private AdapterEx<SpannableString, BottomDialogVH> mAdapter;

    public BottomDialog(@NonNull Context context, @Nullable OnDialogItemClickListener l) {
        super(context);

        mListener = l;
    }

    @Override
    public void initData() {
        mAdapter = new AdapterEx<SpannableString, BottomDialogVH>() {

            @Override
            public int getConvertViewResId() {
                return R.layout.layout_dialog_bottom_item;
            }

            @Override
            protected void refreshView(int position, BottomDialogVH holder) {
                holder.getTv().setText(getItem(position));
            }
        };
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_bottom;
    }

    @Override
    public void findViews() {
        mLv = findView(R.id.dialog_bottom_layout_content);
    }

    @Override
    public void setViews() {
        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(this);

        setGravity(Gravity.BOTTOM);
    }

    public void addItem(String text, @ColorInt int color) {
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAdapter.add(ss);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            mListener.onDialogItemClick(position);
        }
        dismiss();
    }

    public interface OnDialogItemClickListener {
        void onDialogItemClick(int position);
    }

    public class BottomDialogVH extends ViewHolderEx {

        public BottomDialogVH(@NonNull View convertView) {
            super(convertView);
        }

        public TextView getTv() {
            return getView(R.id.dialog_bottom_tv);
        }
    }

}
