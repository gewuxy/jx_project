package yy.doctor.ui.frag.meeting.course;

import android.graphics.Color;

import inject.annotation.router.Route;
import yy.doctor.R;

/**
 * @auther yuansui
 * @since 2017/6/7
 */
@Route
public class AudioCourseFrag extends PicAudioCourseFrag {

    @Override
    public void setViews() {
        getIvHolder().setImageResource(R.drawable.meeting_record_audio_bg);
        setAudio();
        mLayout.setOnRootTouchListener(this);
        setBackgroundColor(Color.TRANSPARENT);
    }
}
