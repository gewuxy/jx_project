package jx.csp.ui.activity.edit;

import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Theme;
import jx.csp.model.main.Meet;
import jx.csp.network.NetFactory;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.util.TextUtil;

/**
 * 编辑会议
 *
 * @auther : GuoXuan
 * @since : 2018/2/7
 */
@Route
public class EditMeetActivity extends BaseEditActivity {

    private final int KSave = 21;

    private TextView mTvSave;

    @Arg
    String mCourseId;

    @Override
    public void initData() {
        super.initData();

        mMeetId = mCourseId;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvSave = findView(R.id.editor_tv_save);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mTvSave);
        titleState(false);
    }

    @Override
    protected int getFooterId() {
        return R.layout.layout_editor_footer_edit;
    }

    @Override
    protected void onClick(int id) {
        switch (id) {
            case R.id.editor_tv_save: {
                refresh(AppConfig.RefreshWay.dialog);
                exeNetworkReq(KSave, NetFactory.update(mMeetId, getTitleText(), mThemeId, mMusicId));
            }
            break;
        }
    }

    @Override
    protected void titleState(boolean notEmpty) {
        mTvSave.setEnabled(notEmpty);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            switch (id) {
                case KReqTheme: {
                    setViewState(DecorViewEx.ViewState.normal);
                    Editor editor = (Editor) r.getData();
                    if (editor == null) {
                        return;
                    }
                    Meet m = editor.get(Editor.TEditor.course);
                    if (m != null) {
                        String title = m.getString(Meet.TMeet.title);
                        setTitleText(title);
                        boolean enabled = TextUtil.isNotEmpty(title);
                        titleState(enabled);
                    }
                    Theme t = editor.get(Editor.TEditor.theme);
                    if (t != null) {
                        mThemeId = t.getInt(Theme.TTheme.imageId);
                        String themeName = t.getString(Theme.TTheme.imgName);
                        if (TextUtil.isNotEmpty(themeName)) {
                            setThemeText(themeName);
                        }
                        mMusicId = t.getInt(Theme.TTheme.musicId);
                        String musicName = t.getString(Theme.TTheme.name);
                        long time = t.getLong(Theme.TTheme.duration, 0);
                        setMusic(musicName, time);
                    }
                    List<Theme> list = editor.getList(Editor.TEditor.imageList);
                    if (mThemeId != Constants.KInvalidValue && list != null && !list.isEmpty()) {
                        for (Theme theme : list) {
                            if (theme.getInt(Theme.TTheme.id) == mThemeId) {
                                theme.put(Theme.TTheme.select, true);
                                break;
                            }
                        }
                    }
                    setData(list);
                }
                break;
                case KSave: {
                    stopRefresh();
                    finish();
                }
                break;
            }
        } else {
            onNetworkError(id, r.getError());
        }
    }

}
