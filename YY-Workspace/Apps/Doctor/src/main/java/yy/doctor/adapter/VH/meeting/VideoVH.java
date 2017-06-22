package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class VideoVH extends ViewHolderEx {
    public VideoVH(@NonNull View convertView) {
        super(convertView);
    }

    public RelativeLayout getLayout() {
        return getView(R.id.video_category_layout);
    }
    
    public TextView getTvName() {
        return getView(R.id.video_category_tv_name);
    }

    public TextView getTvStudy() {
        return getView(R.id.video_category_tv_study);
    }
}
