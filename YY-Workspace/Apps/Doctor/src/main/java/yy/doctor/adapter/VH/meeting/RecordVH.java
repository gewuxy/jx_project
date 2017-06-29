package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author : guoxuan
 * @since : 2017/4/26
 */

public class RecordVH extends ViewHolderEx {
    public RecordVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIvPic() {
        return getView(R.id.meeting_record_pic_iv_image);
    }

    public NetworkImageView getIvVideo() {
        return getView(R.id.meeting_record_video_iv);
    }

    public ImageView getIvPicAudio() {
        return getView(R.id.meeting_record_pic_iv_audio);
    }

    public LinearLayout getLayoutAudio() {
        return getView(R.id.meeting_record_layout_audio);
    }

    public ImageView getIvAudio() {
        return getView(R.id.meeting_record_iv_audio);
    }

    public TextView getTvAudio() {
        return getView(R.id.meeting_record_tv_audio);
    }
}
