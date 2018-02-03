package jx.csp.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;

/**
 * @auther HuoXuYu
 * @since 2018/2/3
 */

public class MusicVH extends ViewHolderEx {

    public MusicVH(@NonNull View convertView) {
        super(convertView);
    }

    public ImageView getIvPlayState() {
        return getView(R.id.music_iv_play_state);
    }

    public ImageView getIvSelect() {
        return getView(R.id.music_iv_select);
    }

    public TextView getTvName() {
        return getView(R.id.music_tv_name);
    }

    public TextView getTvTime() {
        return getView(R.id.music_tv_time);
    }


}
