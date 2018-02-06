package jx.csp.ui.activity.main;

import android.content.Intent;

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
import lib.jx.ui.activity.base.BaseListActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;

/**
 * 添加背景音乐页面
 *
 * @author CaiXiang
 * @since 2018/2/6
 */

public class SelectBgMusicActivity extends BaseListActivity<Music, MusicAdapter> implements OnPlayStateListener {

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.add_music, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().setListener(this);
        refresh(RefreshWay.embed);
        exeNetworkReq(MeetingAPI.editor().type(MusicType.music).showType(1).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Editor.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {

        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            Editor res = (Editor) r.getData();
            if (res != null) {
                setData(res.getList(TEditor.musicList));
            }
        } else {
            setViewState(ViewState.error);
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
