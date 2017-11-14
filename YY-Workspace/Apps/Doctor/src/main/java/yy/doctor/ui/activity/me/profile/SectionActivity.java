package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.model.MapList;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.me.Section;
import yy.doctor.model.me.Section.TSection;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.RegisterAPI;
import yy.doctor.network.NetworkApiDescriptor.UserAPI;
import yy.doctor.ui.frag.user.SectionCategoryFrag;
import yy.doctor.ui.frag.user.SectionCategoryFrag.OnCategoryListener;
import yy.doctor.ui.frag.user.SectionNameFrag;
import yy.doctor.ui.frag.user.SectionNameFrag.OnSectionListener;
import yy.doctor.util.Util;

/**
 * 专科页面
 *
 * @auther Huoxuyu
 * @since 2017/7/17
 */

public class SectionActivity extends BaseActivity implements OnCategoryListener, OnSectionListener {

    private final int KIdGet = 1;
    private final int KIdCommit = 2;

    private SectionCategoryFrag mSectionCategoryFrag;
    private SectionNameFrag mSectionNameFrag;

    private MapList<String, List<String>> mSections;//全部科室数据(分类后)

    private String mCategory;
    private String mCategoryName;

    @Override
    public void initData(Bundle state) {
        mCategory = "内科系统";
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

        refresh(RefreshWay.embed);
        exeNetworkReq(KIdGet, RegisterAPI.specialty().build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KIdGet) {
            return JsonParser.evs(resp.getText(), Section.class);
        } else if (id == KIdCommit)  {
            return JsonParser.error(resp.getText());
        } else {
            return super.onNetworkResponse(id, resp);
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (id == KIdGet) {
            //全部数据
            if (r.isSucceed()) {
                //把相同的过滤掉
                Result<Section> showSection = new Result<>();
                //隶属列表(一级)
                List<Section> categories = r.getList();
                mSections = new MapList<>();
                String category;//科室隶属
                String name;//科室名称
                List<Section> categoryData = new ArrayList<>(); //存放一级科室名称（无重复）
                List<String> sections = null;//隶属的科室列表(二级)
                //按隶属分类
                for (Section section : categories) {
                    category = section.getString(TSection.category);
                    name = section.getString(TSection.name);
                    sections = mSections.getByKey(category);
                    if (sections == null) {
                        categoryData.add(section);
                        sections = new ArrayList<>();
                        mSections.add(category, sections);
                        showSection.add(section);
                    }
                    sections.add(name);
                }
                mSectionCategoryFrag.setData(categoryData);
                mSectionNameFrag.setSection(mSections.get(0));

                setViewState(ViewState.normal);
            } else {
                onNetworkError(id, r.getError());
            }
        } else if (id == KIdCommit) {
            if (r.isSucceed()) {
                Profile.inst().put(TProfile.category, mCategory);
                Profile.inst().put(TProfile.name, mCategoryName);
                Profile.inst().saveToSp();

                Intent intent = new Intent()
                        .putExtra(Extra.KName, mCategory)
                        .putExtra(Extra.KData, mCategoryName);
                setResult(RESULT_OK, intent);
                finish();
                showToast(R.string.user_save_success);
            } else {
                onNetworkError(id, r.getError());
            }
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
            exeNetworkReq(KIdGet, RegisterAPI.specialty().build());
        }
        return true;
    }

    @Override
    public void onCategorySelected(int position, String category) {
        mSectionNameFrag.setSection(mSections.get(position));
        mCategory = category;
    }

    @Override
    public void onSectionSelected(int position, String name) {
        mCategoryName = name;

        if (Profile.inst().isLogin()) {
            refresh(RefreshWay.dialog);
            exeNetworkReq(KIdCommit, UserAPI.modify()
                    .category(mCategory)
                    .name(mCategoryName)
                    .build());
        } else {
            Intent intent = new Intent()
                    .putExtra(Extra.KName, mCategory)
                    .putExtra(Extra.KData, mCategoryName);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

}
