package yy.doctor.adapter;

import android.support.annotation.IntDef;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.RecordVH;
import yy.doctor.model.meet.Detail;
import yy.doctor.model.meet.Detail.TDetail;

/**
 * 会议记录的Adapter
 *
 * @author : guoxuan
 * @since : 2017/4/26
 */

public class RecordAdapter extends MultiAdapterEx<Detail, RecordVH> {

    private String mImgUrl;
    private String mAudioUrl;
    private String mVideoUrl;

    @IntDef({
            RecordType.pic,
            RecordType.video,
            RecordType.sound,
    })
    private @interface RecordType {
        int pic = 0;
        int video = 1;
        int sound = 2;
    }


    @Override
    public int getConvertViewResId(int itemType) {
        switch (itemType) {
            case RecordType.pic:
                return R.layout.layout_meeting_record_pic;
            case RecordType.video:
                return R.layout.layout_meeting_record_video;
            case RecordType.sound:
                return R.layout.layout_meeting_record_sound;
        }
        return R.layout.layout_meeting_record_video;
    }

    @Override
    protected void initView(int position, RecordVH holder, int itemType) {
        switch (itemType) {
            case RecordType.pic:
                break;
            case RecordType.video:
                break;
            case RecordType.sound:
                break;
        }
    }

    @Override
    protected void refreshView(int position, RecordVH holder, int itemType) {
        switch (itemType) {
            case RecordType.pic:
                holder.getIv().placeHolder(R.drawable.meeting_record_image_bg).url(mImgUrl).load();
                break;
            case RecordType.video:
                break;
            case RecordType.sound:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        mImgUrl = getData().get(position).getString(TDetail.imgUrl);
        mAudioUrl = getData().get(position).getString(TDetail.audioUrl);
        mVideoUrl = getData().get(position).getString(TDetail.videoUrl);

        if (!TextUtil.isEmpty(mVideoUrl)) { // 有视频
            return RecordType.video;
        } else if (!TextUtil.isEmpty(mImgUrl)) { // 有图片
            return RecordType.pic;
        } else { // 只有音频
            return RecordType.sound;
        }
    }

    @Override
    public int getViewTypeCount() {
        return RecordType.class.getDeclaredFields().length;
    }
}
