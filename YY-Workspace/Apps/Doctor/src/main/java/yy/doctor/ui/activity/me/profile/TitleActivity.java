package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
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
import yy.doctor.network.NetFactory;
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

    private TitleGradeFrag mTitleGradeFrag;
    private TitleCategoryFrag mTitleCategoryFrag;

    private final int KRegister = 0;
    private final int KModify = 1;

    private TProfile mEnum;
    String mTitle;
    private String mGrade;
    private String mCategory;

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
        if (Profile.inst().isLogin()) {
            exeNetworkReq(KModify, NetFactory.newModifyBuilder().title(mGrade + mCategory).build());
        }else {
            exeNetworkReq(KRegister, NetFactory.title());
        }
    }


    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Title.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KRegister) {//注册界面的职称
            Result<Title> r = (Result<Title>) result;
            if (r.isSucceed()) {
                Title data = r.getData();
                List<String> title = data.getList(TTitle.title);
                mTitleGradeFrag.setData(title);

                List<String> grade = data.getList(TTitle.grade);
                mTitleCategoryFrag.setData(grade);

                setViewState(ViewState.normal);

            } else {
                stopRefresh();
                showToast(r.getError());
            }
        }else if (id == KModify) {//我的资料的职称
            Result<Title> r = (Result<Title>) result;
            if (r.isSucceed()) {
                Title data = r.getData();
                List<String> title = data.getList(TTitle.title);
                mTitleGradeFrag.setData(title);

                List<String> grade = data.getList(TTitle.grade);
                mTitleCategoryFrag.setData(grade);

                setViewState(ViewState.normal);

            } else {
                stopRefresh();
                showToast(r.getError());
            }
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public void onGradeSelected(int position, String grade) {
        mGrade = grade;
        YSLog.d(TAG, "grade = " + grade);
    }

    @Override
    public void onCategorySelected(int position, String category) {
        mCategory = category;
        mTitle = mGrade + " " + category;
        Profile.inst().put(mEnum, mTitle);
        Profile.inst().saveToSp();

        Intent i = new Intent()
                .putExtra(Extra.KProvince, mGrade)
                .putExtra(Extra.KCity, category);
        YSLog.d(TAG, "category = " + category);
        setResult(RESULT_OK, i);
        finish();
    }
}
