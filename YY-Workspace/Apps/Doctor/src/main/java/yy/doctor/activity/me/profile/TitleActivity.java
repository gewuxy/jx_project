package yy.doctor.activity.me.profile;

import android.content.Intent;
import android.support.annotation.NonNull;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.TitleCategoryFrag;
import yy.doctor.frag.TitleCategoryFrag.OnCategoryListener;
import yy.doctor.frag.TitleGradeFrag;
import yy.doctor.frag.TitleGradeFrag.OnGradeListener;
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
    private String mGrade;

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
        Util.addBackIcon(bar, R.string.title, this);
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
    }

    @Override
    public void onGradeSelected(int position, String grade) {
        mGrade = grade;
        YSLog.d(TAG, "grade = " + grade);
    }

    @Override
    public void onCategorySelected(int position, String category) {
        Intent i = new Intent();
        i.putExtra(Extra.KProvince, mGrade);
        i.putExtra(Extra.KCity, category);
        YSLog.d(TAG, "category = " + category);
        setResult(RESULT_OK, i);
        finish();
    }

}
