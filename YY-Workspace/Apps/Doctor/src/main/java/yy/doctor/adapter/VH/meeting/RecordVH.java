package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * 日期 : 2017/4/26
 * : guoxuan
 */

public class RecordVH extends ViewHolderEx {
    public RecordVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIvPic() {
        return getView(R.id.meeting_record_pic_iv_image);
    }

    public ImageView getIvPicAudio() {
        return getView(R.id.meeting_record_pic_iv_audio);
    }
}
