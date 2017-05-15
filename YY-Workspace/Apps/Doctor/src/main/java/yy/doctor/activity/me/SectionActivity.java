package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.ListView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.LogMgr;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.ListResp;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.DepartmentsAdapter;
import yy.doctor.adapter.DepartmentsDetailAdapter;
import yy.doctor.model.Section;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 医院科室页面
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
public class SectionActivity extends BaseActivity {

    //TODO:adapter
    private ListView mLvSection;
    private ListView mLvSectionDetail;

    private DepartmentsAdapter mSectionAdapter;
    private DepartmentsDetailAdapter mSectionDetailAdapter;

    @Override
    public void initData() {
        mSectionAdapter = new DepartmentsAdapter(this);
        mSectionDetailAdapter = new DepartmentsDetailAdapter(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_departments;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "科室", this);
    }

    @Override
    public void findViews() {
        mLvSection = findView(R.id.departments_list);
        mLvSectionDetail = findView(R.id.departments_detail_list);
    }

    @Override
    public void setViews() {
        mLvSection.setAdapter(mSectionAdapter);
        mLvSection.setOnItemClickListener((parent, view, position, id) -> {
            mSectionAdapter.setSelectItem(position);
            mSectionDetailAdapter.setmList(position);
        });

        mLvSectionDetail.setAdapter(mSectionDetailAdapter);
        mLvSectionDetail.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            String str = mSectionDetailAdapter.getDepartmentsDetail(position);
            intent.putExtra(Extra.KData, str);
            setResult(RESULT_OK, intent);
            finish();
        });

        exeNetworkReq(0, NetFactory.section());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.evs(r.getText(), Section.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        ListResp<Section> r = (ListResp<Section>) result;
        if (r.isSucceed()) {
            List<Section> data = r.getData();
            for (Section section : data) {
                LogMgr.d("section",section.getString(Section.TSection.name));
            }
        } else {
            showToast(r.getError());
        }
    }
}
