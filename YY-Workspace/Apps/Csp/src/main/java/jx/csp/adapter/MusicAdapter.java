package jx.csp.adapter;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.MusicVH;
import jx.csp.model.editor.Music;
import jx.csp.model.editor.Music.TMusic;
import jx.csp.util.Util;
import lib.ys.adapter.AdapterEx;

/**
 * @auther HuoXuYu
 * @since 2018/2/3
 */

public class MusicAdapter extends AdapterEx<Music, MusicVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_music_select;
    }

    @Override
    protected void refreshView(int position, MusicVH holder) {
        Music item = getItem(position);
        holder.getTvName().setText(item.getString(TMusic.name));
        holder.getTvTime().setText(Util.getSpecialTimeFormat(item.getLong(TMusic.duration), "'", "''"));

        View playState = holder.getIvPlayState();
        playState.setSelected(item.getBoolean(TMusic.select));

        setOnViewClickListener(position, playState);
        setOnViewClickListener(position, holder.getIvSelect());
    }
}
