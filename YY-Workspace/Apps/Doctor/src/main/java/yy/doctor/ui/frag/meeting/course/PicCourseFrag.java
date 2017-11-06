package yy.doctor.ui.frag.meeting.course;

import android.graphics.Color;

import inject.annotation.router.Route;
import yy.doctor.model.meet.Submit.TSubmit;

/**
 * PPT图片
 *
 * @auther : GuoXuan
 * @since : 2017/6/3
 */
@Route
public class PicCourseFrag extends PicAudioCourseFrag {

    @Override
    public void setViews() {
        setPic();

        getLayout().setOnRootTouchListener(this);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void loadFinish() {
        super.loadFinish();

        getSubmit().put(TSubmit.finished, true); // 图片加载完成就算完成
    }
}
