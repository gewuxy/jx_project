package jx.csp.adapter;

import android.view.View;
import android.widget.ImageView;

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

    private OnPlayStateListener mListener;

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

    @Override
    protected void onViewClick(int position, View v) {
        switch (v.getId()) {
            case R.id.music_iv_play_state: {
                ImageView iv = getCacheVH(position).getIvPlayState();
                boolean selected = iv.isSelected();
                if (mListener != null) {
                    mListener.onPlayState(position, !selected);
                    iv.setSelected(!selected);
                }
            }
            break;
            case R.id.music_iv_select: {
                ImageView iv = getCacheVH(position).getIvSelect();
                boolean selected = iv.isSelected();
                if (mListener != null) {
                    mListener.onSelect(position);
                    iv.setSelected(!selected);
                }
            }
            break;
        }
    }

    public void setListener(OnPlayStateListener l) {
        mListener = l;
    }

    public interface OnPlayStateListener {
        void onPlayState(int position, boolean isSelected);
        void onSelect(int position);
    }
}
