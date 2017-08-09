package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.frag.SectionCategoryFrag;
import yy.doctor.ui.frag.SectionCategoryFrag.OnCategoryListener;
import yy.doctor.ui.frag.SectionNameFrag;
import yy.doctor.ui.frag.SectionNameFrag.OnSectionListener;
import yy.doctor.util.Util;

/**
 * 专科页面
 *
 * @auther Huoxuyu
 * @since 2017/7/17
 */

public class SectionActivity extends BaseActivity implements OnCategoryListener, OnSectionListener {

    private final int KIdGet = 0;
    private final int KIdCommit = 1;

    private SectionCategoryFrag mSectionCategoryFrag;
    private SectionNameFrag mSectionNameFrag;

    private String mCategory;
    private String mCategoryName;


    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_section;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.specialized, this);
    }

    @Override
    public void findViews() {
        mSectionCategoryFrag = findFragment(R.id.section_frag_category);
        mSectionNameFrag = findFragment(R.id.section_frag_name);
    }

    @Override
    public void setViews() {
        mSectionCategoryFrag.setCategoryListener(this);
        mSectionNameFrag.setListener(this);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdCommit) {
            return JsonParser.error(r.getText());
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdCommit) {
            Result r = (Result) result;
            if (r.isSucceed()) {
                Profile.inst().put(TProfile.category, mCategory);
                Profile.inst().put(TProfile.name, mCategoryName);
                Profile.inst().saveToSp();

                stopRefresh();

                Intent intent = new Intent()
                        .putExtra(Extra.KName, mCategory)
                        .putExtra(Extra.KData, mCategoryName);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
    }

    @Override
    public void onCategorySelected(int position, String category, List<String> names) {
        mSectionNameFrag.setSection(names);
        mCategory = category;
        YSLog.d("yaya", mCategory);
    }

    @Override
    public void onSectionSelected(int position, String name) {
        mCategoryName = name;

        refresh(RefreshWay.dialog);
        exeNetworkReq(KIdCommit, NetFactory.newModifyBuilder()
                .category(mCategory)
                .name(mCategoryName)
                .build());
    }

}
