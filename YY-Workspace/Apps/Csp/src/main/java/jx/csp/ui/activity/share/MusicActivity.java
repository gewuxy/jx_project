package jx.csp.ui.activity.share;

import android.content.Intent;

import java.util.List;

import jx.csp.Extra;
import jx.csp.R;
import jx.csp.adapter.MusicAdapter;
import jx.csp.adapter.MusicAdapter.OnPlayStateListener;
import jx.csp.constant.MusicType;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Editor.TEditor;
import jx.csp.model.editor.Music;
import jx.csp.model.editor.Music.TMusic;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseSRListActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.other.NavBar;

/**
 * 背景音乐
 *
 * @auther HuoXuYu
 * @since 2018/2/3
 */

public class MusicActivity extends BaseSRListActivity<Music, MusicAdapter> implements OnPlayStateListener {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.editor_music, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(MeetingAPI.editor(MusicType.music).build());

    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().setListener(this);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        super.onNetworkResponse(id, resp);
        return JsonParser.ev(resp.getText(), Editor.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        super.onNetworkSuccess(id, r);

        if (r.isSucceed()) {
            Editor editor = (Editor) r.getData();
            if (editor != null) {
                List<Music> list = editor.getList(TEditor.musicList);
                setData(list);
            }
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onPlayState(int position, boolean isSelected) {

    }

    @Override
    public void onSelect(int position) {
        Music item = getItem(position);
        //传给编辑页面
        Intent intent = new Intent()
                .putExtra(Extra.KData, item.getString(TMusic.name))
                .putExtra(Extra.KLimit, item.getString(TMusic.duration))
                .putExtra(Extra.KId, item.getString(TMusic.id));
        setResult(RESULT_OK, intent);
        finish();
    }

}
