package yy.doctor.model.meet;

import android.support.annotation.IntDef;

import lib.ys.model.EVal;
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

        play, // 是否在播放
        studyTime, // 学习时间
    }

    @IntDef({
            CourseType.un_know,
            CourseType.pic_audio,
            CourseType.audio,
            CourseType.pic,
            CourseType.video,
    })
    public @interface CourseType {
        int un_know = -1;
        int pic_audio = 0;
        int audio = 1;
        int pic = 2;
        /**
         * @deprecated 这版本没有视频
         */
        int video = 3;
    }

    /**
     * 自定义字段
     * {@link CourseType}
     */
    private int mType = CourseType.un_know;

    /**
     * 获取课件类型
     *
     * @return
     */
    public int getType() {
        if (mType == CourseType.un_know) {
            if (getString(TCourse.videoUrl).isEmpty()) {
                // 不是视频
                if (getString(TCourse.audioUrl).isEmpty()) {
                    mType = CourseType.pic;
                } else if (getString(TCourse.imgUrl).isEmpty()) {
                    mType = CourseType.audio;
                } else {
                    mType = CourseType.pic_audio;
                }
            } /*else {
                mType = CourseType.video;
            }*/
        }
        return mType;
    }
}
