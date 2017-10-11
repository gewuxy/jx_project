package yy.doctor.ui.frag.meeting.course;

import inject.annotation.router.Route;
import yy.doctor.R;

/**
 * PPT音频
 *
 * @auther yuansui
 * @since 2017/6/7
 */
@Route
public class AudioCourseFrag extends PicAudioCourseFrag {

    @Override
    public void setViews() {
        getIvHolder().setImageResource(R.drawable.meeting_record_audio_bg);
        getLayout().setOnRootTouchListener(this);
        setBackgroundResource(R.color.app_nav_bar_bg);
    }
}
