package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
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

    private SectionCategoryFrag mSectionCategoryFrag;
    private SectionNameFrag mSectionNameFrag;
    private String mCategory;


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
    public void onCategorySelected(int position, List<String> names) {
        mSectionNameFrag.setSection(names);
        mCategory = names.get(position);
        YSLog.d("yaya",mCategory);
    }

    @Override
    public void onSectionSelected(int position, String name) {

        Intent intent = new Intent();
        intent.putExtra(Extra.KName,mCategory);
        intent.putExtra(Extra.KData,name);
        setResult(RESULT_OK, intent);
        finish();
    }

}
