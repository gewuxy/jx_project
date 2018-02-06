package jx.csp.adapter;

import android.view.View;
import android.widget.ImageView;

import jx.csp.R;
import jx.csp.adapter.VH.MusicVH;
import jx.csp.constant.Constants;
import jx.csp.model.editor.Music;
import jx.csp.model.editor.Music.TMusic;
import jx.csp.util.Util;
import lib.ys.adapter.AdapterEx;

/**
 * @auther HuoXuYu
 * @since 2018/2/3
 */

public class MusicAdapter extends AdapterEx<Music, MusicVH> {

    private int mLastPlayPos = Constants.KInvalidValue;

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_music_select;
    }

    @Override
    protected void refreshView(int position, MusicVH holder) {
        Music item = getItem(position);
        holder.getTvName().setText(item.getString(TMusic.name));
        holder.getTvTime().setText(Util.getSpecialTimeFormat(item.getLong(TMusic.duration), "'", "''"));

        ImageView playState = holder.getIvPlayState();
        playState.setSelected(item.getBoolean(TMusic.play));
        setOnViewClickListener(position, playState);

        ImageView select = holder.getIvSelect();
        select.setSelected(item.getBoolean(TMusic.select));
        setOnViewClickListener(position, select);
    }

    @Override
    protected void onViewClick(int position, View v) {
        switch (v.getId()) {
            case R.id.music_iv_play_state: {
                if (mLastPlayPos == position) {
                    boolean play = !getItem(position).getBoolean(TMusic.play, false);
                    getItem(position).put(TMusic.play, play);
                } else {
                    if (mLastPlayPos != Constants.KInvalidValue) {
                        getItem(mLastPlayPos).put(TMusic.play, false);
                        invalidate(mLastPlayPos);
                    }
                    mLastPlayPos = position;
                    getItem(position).put(TMusic.play, true);
                }
                invalidate(position);
            }
            break;
            case R.id.music_iv_select: {
                getItem(position).put(TMusic.select, true);
                invalidate(position);
            }
            break;
        }
    }
}
