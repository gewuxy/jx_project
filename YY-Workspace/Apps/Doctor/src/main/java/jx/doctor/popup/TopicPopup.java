package jx.doctor.popup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import lib.ys.ui.other.PopupWindowEx;
import jx.doctor.R;

/**
 * 第一次进入考试 / 问卷
 *
 * @auther WangLan
 * @since 2017/6/23
 */

public class TopicPopup extends PopupWindowEx {

    private View mLayout;
    private ImageView mPopSlide; // 中间的view
    private ImageView mPopCheck; // 底下的view

    public TopicPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_topic_popup;
    }

    @Override
    public void findViews() {
        mPopSlide = findView(R.id.exam_pop_slide);
        mPopCheck = findView(R.id.exam_pop_check);
        mLayout = findView(R.id.exam_pop_layout);
    }

    @Override
    public void setViews() {
        mLayout.setOnClickListener(this);
    }

    @Override
    public int getWindowWidth() {
        return MATCH_PARENT;
    }

    @Override
    public int getWindowHeight() {
        return MATCH_PARENT;
    }

    public void setSlide(@DrawableRes int resId) {
        mPopSlide.setImageResource(resId);
    }

    public void setCheck(@DrawableRes int resId) {
        mPopCheck.setImageResource(resId);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
