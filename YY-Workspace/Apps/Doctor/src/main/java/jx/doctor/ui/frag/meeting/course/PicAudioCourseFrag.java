package jx.doctor.ui.frag.meeting.course;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import inject.annotation.router.Route;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.util.TextUtil;
import lib.ys.view.photoViewer.NetworkPhotoView;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.util.Util;
import jx.doctor.view.RootLayout;
import jx.doctor.view.RootLayout.OnRootTouchListener;

/**
 * PPT音频+图片
 *
 * @auther yuansui
 * @since 2017/6/7
 */
@Route
public class PicAudioCourseFrag extends BaseCourseFrag implements OnRootTouchListener {

    private NetworkPhotoView mIvPPT;
    private ImageView mIvHolder;
    private RootLayout mLayout;

    protected ImageView getIvHolder() {
        return mIvHolder;
    }

    protected RootLayout getLayout() {
        return mLayout;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_ppt_course_pic;
    }

    @Override
    public void findViews() {
        mIvPPT = findView(R.id.meeting_course_pic_iv);
        mIvHolder = findView(R.id.meeting_course_pic_iv_place_holder);
        mLayout = findView(R.id.meeting_course_pic_layout);
    }

    @Override
    public void setViews() {
        setPic();

        mLayout.setOnRootTouchListener(this);
        setBackgroundColor(Color.TRANSPARENT);
    }

    protected void setPic() {
        String imgUrl = Util.convertUrl(getCourse().getString(TCourse.imgUrl));

        if (!TextUtil.isEmpty(imgUrl)) {
            mIvPPT.url(imgUrl)
                    .listener(new NetworkImageListener() {
                        @Override
                        public void onImageSet(ImageInfo info) {
                            loadFinish();
                        }
                    })
                    .load();
        }
    }

    /**
     * 加载成功
     */
    protected void loadFinish() {
        // 隐藏默认图
        goneView(mIvHolder);
    }

    public void setScale() {
        mIvPPT.setScale(1, false);
    }

    @Override
    public String getUrl() {
        return Util.convertUrl(getCourse().getString(TCourse.audioUrl));
    }

    @Override
    public void onTouchUp() {
        clickFrag();
    }

    public void refresh() {
        setPic();
    }
}
