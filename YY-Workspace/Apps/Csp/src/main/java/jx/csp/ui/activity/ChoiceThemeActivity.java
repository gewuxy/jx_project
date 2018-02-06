package jx.csp.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.adapter.share.EditorAdapter;
import jx.csp.constant.MusicType;
import jx.csp.model.editor.Editor;
import jx.csp.model.editor.Theme;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.config.AppConfig;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.ui.other.NavBar;

/**
 * 添加(选择)主题界面
 *
 * @auther : GuoXuan
 * @since : 2018/2/6
 */
@Route
public class ChoiceThemeActivity extends BaseRecyclerActivity<Theme, EditorAdapter> implements
        MultiAdapterEx.OnAdapterClickListener {

    private final int KEditor = 1;
    private final int KSave = 2;

    private final int KClearInfo = 0; // 清空

    private int mImgId; //主题图片id

    @Arg
    String mMeetId; // meetId

    @Arg
    String mPreviewUrl; // 预览上的图片

    private View mLayoutSave;

    @Override
    public int getContentViewId() {
        return R.layout.activity_theme;
    }

    @Override
    public void initData() {
        mImgId = KClearInfo;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.add_theme, this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutSave = findView(R.id.editor_tv_save);
    }

    @Override
    public void setViews() {
        super.setViews();

        // 获取主题皮肤
        refresh(AppConfig.RefreshWay.embed);
        getDataFromNet();
        setOnAdapterClickListener(this);
        setOnClickListener(mLayoutSave);
        mLayoutSave.setEnabled(false);
    }

    @Override
    protected RecyclerView.LayoutManager initLayoutManager() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return manager;
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KEditor) {
            return JsonParser.ev(resp.getText(), Editor.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            switch (id) {
                case KEditor: {
                    setViewState(DecorViewEx.ViewState.normal);
                    Editor editor = (Editor) r.getData();
                    if (editor != null) {
                        List<Theme> list = editor.getList(Editor.TEditor.imageList);
                        setData(list);
                    }
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

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        if (id == KEditor) {
            setViewState(DecorViewEx.ViewState.error);
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(AppConfig.RefreshWay.embed);
            getDataFromNet();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editor_tv_save: {
                //分享进入的保存按钮
                refresh(AppConfig.RefreshWay.dialog);
                exeNetworkReq(KSave, NetworkApiDescriptor.MeetingAPI.update(mMeetId).imgId(mImgId).build());
            }
            break;
        }
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.editor_theme_layout: {
                boolean select = getItem(position).getBoolean(Theme.TTheme.select);
                mImgId = select ? getItem(position).getInt(Theme.TTheme.id) : KClearInfo;
                mLayoutSave.setEnabled(mImgId != KClearInfo);
            }
            break;
            case R.id.editor_preview_tv_item: {
                // 预览主题皮肤
                String theme = getItem(position).getString(Theme.TTheme.imgUrl);
                UISetter.previewTheme(this, theme, mPreviewUrl);
            }
            break;
        }
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KEditor, NetworkApiDescriptor.MeetingAPI.editor().type(MusicType.theme).build());
    }

}
