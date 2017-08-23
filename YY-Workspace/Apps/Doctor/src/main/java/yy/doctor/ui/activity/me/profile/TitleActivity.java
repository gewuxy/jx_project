package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.Title;
import yy.doctor.model.Title.TTitle;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.RegisterAPI;
import yy.doctor.network.NetworkAPISetter.UserAPI;
import yy.doctor.ui.frag.TitleCategoryFrag;
import yy.doctor.ui.frag.TitleCategoryFrag.OnCategoryListener;
import yy.doctor.ui.frag.TitleGradeFrag;
import yy.doctor.ui.frag.TitleGradeFrag.OnGradeListener;
import yy.doctor.util.Util;

/**
 * 医生职称
 *
 * @author CaiXiang
 * @since 2017/5/24
 */

public class TitleActivity extends BaseActivity implements OnGradeListener, OnCategoryListener {

    private final int KIdGet = 0;
    private final int KIdCommit = 1;

    private TitleGradeFrag mTitleGradeFrag;
    private TitleCategoryFrag mTitleCategoryFrag;

    private String mGrade;
    private String mTitle;

    @Override
    public void initData() {
        mGrade = getString(R.string.high_grade);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_title;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.user_title, this);
    }

    @Override
    public void findViews() {
        mTitleGradeFrag = findFragment(R.id.title_frag_grade);
        mTitleCategoryFrag = findFragment(R.id.title_frag_category);
    }

    @Override
    public void setViews() {
        mTitleGradeFrag.setGradeListener(this);
        mTitleCategoryFrag.setCategoryListener(this);

        refresh(RefreshWay.embed);
        exeNetworkReq(KIdGet, RegisterAPI.title().build());

    }


    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        switch (id) {
            case KIdGet: {
                return JsonParser.ev(r.getText(), Title.class);
            }
            case KIdCommit: {
                return JsonParser.error(r.getText());
            }
        }
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        switch (id) {
            case KIdGet: {
                Result<Title> r = (Result<Title>) result;
                if (r.isSucceed()) {
                    Title data = r.getData();
                    List<String> title = data.getList(TTitle.title);
                    mTitleGradeFrag.setData(title);

                    List<String> grade = data.getList(TTitle.grade);
                    mTitleCategoryFrag.setData(grade);

                    setViewState(ViewState.normal);
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KIdCommit: {
                Result r = (Result) result;
                if (r.isSucceed()) {
                    Profile.inst().put(TProfile.title, mTitle);
                    Profile.inst().saveToSp();
                    Intent i = new Intent().putExtra(Extra.KData, mTitle);
                    setResult(RESULT_OK, i);
                    finish();
                    showToast(R.string.user_save_success);
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);
        if (id == KIdGet) {
            setViewState(ViewState.error);
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            exeNetworkReq(KIdGet, RegisterAPI.title().build());
        }
        return true;
    }

    @Override
    public void onGradeSelected(int position, String grade) {
        mGrade = grade;
        YSLog.d(TAG, "grade = " + grade);
    }

    @Override
    public void onCategorySelected(int position, String category) {
        mTitle = mGrade + " " + category;

        if (Profile.inst().isLogin()) {
            // 登录了就是在个人中心界面p
            refresh(RefreshWay.dialog);
            exeNetworkReq(KIdCommit, UserAPI.modify().title(mTitle).build());
        } else {
            // 没有登录就是在注册界面
            Intent i = new Intent().putExtra(Extra.KData, mTitle);
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
