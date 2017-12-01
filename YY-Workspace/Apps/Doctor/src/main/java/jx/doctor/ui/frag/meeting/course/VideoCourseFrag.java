package jx.doctor.ui.frag.meeting.course;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import inject.annotation.router.Route;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.util.Util;

/**
 * PPT视频
 *
 * @auther : GuoXuan
 * @since : 2017/6/28
 */
@Route
public class VideoCourseFrag extends BaseCourseFrag {

    private String mVideoUrl;
    private PLVideoTextureView mTextureView;

    public PLVideoTextureView getTextureView() {
        return mTextureView;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_ppt_course_video;
    }

    @Override
    public void findViews() {
        mTextureView = findView(R.id.meeting_course_video_layout_video);
    }

    @Override
    public void setViews() {
        mVideoUrl = Util.convertUrl(getCourse().getString(TCourse.videoUrl));
        setOnClickListener(R.id.meeting_course_video_layout);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        clickFrag();
    }

    @Override
    public String getUrl() {
        return mVideoUrl;
    }

}
