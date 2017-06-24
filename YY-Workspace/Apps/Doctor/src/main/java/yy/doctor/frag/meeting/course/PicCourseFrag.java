package yy.doctor.frag.meeting.course;

import android.graphics.Color;

/**
 * PPT图片
 *
 * @auther : GuoXuan
 * @since : 2017/6/3
 */

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
