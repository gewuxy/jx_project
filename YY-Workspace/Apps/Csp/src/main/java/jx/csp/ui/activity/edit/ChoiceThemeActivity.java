package jx.csp.ui.activity.edit;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.adapter.ThemeAdapter;
import jx.csp.constant.Constants;
import jx.csp.model.editor.AllTheme;
import jx.csp.model.editor.AllTheme.TAllTheme;
import jx.csp.model.editor.Theme;
import jx.csp.model.editor.Theme.TTheme;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
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
public class ChoiceThemeActivity extends BaseRecyclerActivity<Theme, ThemeAdapter> implements
        MultiAdapterEx.OnAdapterClickListener {

    private final int KEditor = 1;
    private final int KSave = 2;

    @Arg()
    String mMeetId; // meetId

    @Arg
    String mPreviewUrl; // 预览上的图片

    @Arg(opt = true)
    int mThemeId; // 主题图片id

    @Arg(opt = true)
    boolean mSave;  // true 表示保存时不请求网络，直接带参数返回

    private int mSelectPos = -1;  // 选择的位置

    @Override
    public int getContentViewId() {
        return R.layout.activity_theme;
    }

    @Override
    public void initData() {
        // do thing
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.add_theme, this);
        TextView tv = bar.addTextViewRight(R.string.save, R.color.text_ace400, v -> {
            if (mSave) {
                if (mSelectPos != -1) {
                    forResult(getItem(mSelectPos));
                }
                finish();
            } else {
                refresh(AppConfig.RefreshWay.dialog);
                exeNetworkReq(KSave, MeetingAPI.selectTheme(mMeetId, mThemeId).build());
            }
        });
        Util.setTextViewBackground(tv);
    }

    @Override
    public void setViews() {
        super.setViews();

        // 获取主题皮肤
        refresh(AppConfig.RefreshWay.embed);
        getDataFromNet();
        setOnAdapterClickListener(this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KEditor, NetworkApiDescriptor.MeetingAPI.theme().build());
    }

    @Override
    protected RecyclerView.LayoutManager initLayoutManager() {
        return new GridLayoutManager(this, 4);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.editor_theme_layout: {
                boolean select = getItem(position).getBoolean(Theme.TTheme.select);
                mThemeId = select ? getItem(position).getInt(Theme.TTheme.id) : 0;
                if (select) {
                    mSelectPos = position;
                } else {
                    mSelectPos = -1;
                }
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
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KEditor) {
            return JsonParser.ev(resp.getText(), AllTheme.class);
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
                    AllTheme theme = (AllTheme) r.getData();
                    if (theme != null) {
                        List<Theme> list = theme.getList(TAllTheme.list);
                        if (mThemeId != Constants.KInvalidValue && list != null && list.size() > 0) {
                            for (Theme t : list) {
                                if (mThemeId == t.getInt(TTheme.id)) {
                                    t.put(TTheme.select, true);
                                    break;
                                }
                            }
                        }
                        setData(list);
                    }
                }
                break;
                case KSave: {
                    stopRefresh();
                    if (mSelectPos != -1) {
                        forResult(getItem(mSelectPos));
                    } else {
                        finish();
                    }
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

    private void forResult(Theme item) {
        Intent intent = new Intent()
                .putExtra(Extra.KData, item.getString(TTheme.imgName))
                .putExtra(Extra.KId, item.getInt(TTheme.id));
        setResult(RESULT_OK, intent);
        finish();
    }

}
