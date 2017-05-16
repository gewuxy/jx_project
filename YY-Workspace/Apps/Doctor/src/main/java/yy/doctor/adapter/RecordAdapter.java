package yy.doctor.adapter;

import android.support.annotation.IntDef;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.RecordVH;

/**
 * 会议记录的Adapter
 *
 * @author : guoxuan
 * @since : 2017/4/26
 */

public class RecordAdapter extends MultiAdapterEx<String, RecordVH> {

    @IntDef({
            RecordType.text,
            RecordType.image,
            RecordType.location,
            RecordType.video,
            RecordType.sound,
    })
    private @interface RecordType {
        int text = 0;
        int image = 1;
        int location = 2;
        int video = 3;
        int sound = 4;
    }


    @Override
    public int getConvertViewResId(int itemType) {
        switch (itemType) {
            case RecordType.text:
                return R.layout.layout_meeting_record_text;
            case RecordType.image:
                return R.layout.layout_meeting_record_image;
            case RecordType.sound:
                return R.layout.layout_meeting_record_sound;
            default:
                break;
        }

        return R.layout.layout_meeting_record_text;
    }

    @Override
    protected void initView(int position, RecordVH holder, int itemType) {
        super.initView(position, holder, itemType);
    }

    @Override
    protected void refreshView(int position, RecordVH holder, int itemType) {
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 0 && position < 3) {
            return RecordType.image;
        } else if (position == 4) {
            return RecordType.sound;
        }
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return RecordType.class.getDeclaredFields().length;
    }
}
