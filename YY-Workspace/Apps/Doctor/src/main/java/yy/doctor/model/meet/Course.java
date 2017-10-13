package yy.doctor.model.meet;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.EVal;
import lib.ys.util.TextUtil;
import yy.doctor.model.meet.Course.TCourse;

/**
 * 微课具体明细信息
 *
 * @author : GuoXuan
 * @since : 2017/5/9
 */

public class Course extends EVal<TCourse> {

    public enum TCourse {
        audioUrl, // 微课音频明细路径
        id, // 微课明细ID
        imgUrl, // 微课图片明细路径
        sort, // 微课明细序号
        videoUrl, // 微课视频明细路径

        /******************
         * 以下是自定义字段
         */

        play, // 是否在播放
        select, // 是否选中
        time, // 播放时间
    }

    @IntDef({
            CourseType.un_know,
            CourseType.pic_audio,
            CourseType.audio,
            CourseType.pic,
            CourseType.video,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CourseType {
        int un_know = -1;
        int pic_audio = 0;
        int audio = 1;
        int pic = 2;
        int video = 3;
    }

    /**
     * 自定义字段
     * {@link CourseType}
     */
    private int mType = CourseType.un_know;

    /**
     * 获取课件类型, 根据url确定类型
     * 优先判断video
     */
    public int getType() {
        if (mType == CourseType.un_know) {
            String imgUrl = getString(TCourse.imgUrl);
            String audioUrl = getString(TCourse.audioUrl);
            String videoUrl = getString(TCourse.videoUrl);

            if (!TextUtil.isEmpty(videoUrl)) {
                // 有视频
                return CourseType.video;
            } else if (!TextUtil.isEmpty(audioUrl)) {
                // 有音频
                if (!TextUtil.isEmpty(imgUrl)) {
                    // 有音频且有图片
                    return CourseType.pic_audio;
                } else {
                    // 只有音频
                    return CourseType.audio;
                }
            } else {
                // 只有图片
                return CourseType.pic;
            }
        }
        return mType;
    }

    /**
     * 是否有音/视频
     */
    public boolean haveMedia() {
        switch (getType()) {
            // 不加break 因为都有多媒体
            case CourseType.pic_audio:
            case CourseType.video:
            case CourseType.audio: {
                return true;
            }
        }
        return false;
    }
}
