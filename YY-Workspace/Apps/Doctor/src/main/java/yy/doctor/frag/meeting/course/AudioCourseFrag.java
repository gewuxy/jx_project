package yy.doctor.frag.meeting.course;

import android.graphics.Color;

/**
 * @auther yuansui
 * @since 2017/6/7
 */

public class AudioCourseFrag extends PicAudioCourseFrag {

    @Override
    public void setViews() {
        setAudio();
        mLayout.setOnRootTouchListener(this);
        setBackgroundColor(Color.TRANSPARENT);
    }
}
