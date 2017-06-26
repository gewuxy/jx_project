package yy.doctor.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import lib.ys.ui.other.PopupWindowEx;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/6/23
 */

public class ExamPopup extends PopupWindowEx {
    private ImageView mPopSlide;
    private ImageView mPopCheck;
    private OnPopListener mLsn;
    public ExamPopup(@NonNull Context context,OnPopListener onPopListener) {
        super(context);
        mLsn = onPopListener;
    }

    @Override
    public void initData() {
        setTouchOutsideDismissEnabled(true);
        setDimEnabled(false);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_exam_popup;
    }

    @Override
    public void findViews() {
        mPopSlide = findView(R.id.exam_pop_slide);
        mPopCheck = findView(R.id.exam_pop_check);
    }

    @Override
    public void setViews() {

    }

    @Override
    public int getWindowWidth() {
        return MATCH_PARENT;
    }

    @Override
    public int getWindowHeight() {
        return MATCH_PARENT;
    }
    public interface OnPopListener {
        void OnPopListener(View view );
    }
}
