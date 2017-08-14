package yy.doctor.ui.frag.meeting.course;

import android.graphics.Color;

import router.annotation.AutoArg;

/**
 * PPT图片
 *
 * @auther : GuoXuan
 * @since : 2017/6/3
 */
@AutoArg
public class PicCourseFrag extends PicAudioCourseFrag {

    @Override
    public void setViews() {
        setPic();
        mLayout.setOnRootTouchListener(this);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean isFinish() {
        return true;
    }
}
