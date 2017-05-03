package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.DepartmentsAdapter;
import yy.doctor.adapter.DepartmentsDetailAdapter;
import yy.doctor.util.Util;

/**
 * 医院科室页面
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
public class DepartmentsActivity extends BaseActivity {

    private ListView mLvDepartments;
    private ListView mLvDepartmentsDetail;

    private DepartmentsAdapter mDepartmentsAdapter;
    private DepartmentsDetailAdapter mDepartmentsDetailAdapter;

    @Override
    public void initData() {

        mDepartmentsAdapter = new DepartmentsAdapter(this);
        mDepartmentsDetailAdapter = new DepartmentsDetailAdapter(this);

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

        mLvDepartments = findView(R.id.departments_list);
        mLvDepartmentsDetail = findView(R.id.departments_detail_list);

    }

    @Override
    public void setViews() {

        mLvDepartments.setAdapter(mDepartmentsAdapter);
        mLvDepartmentsDetail.setAdapter(mDepartmentsDetailAdapter);

        mLvDepartments.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mDepartmentsAdapter.setSelectItem(position);
                mDepartmentsDetailAdapter.setmList(position);

            }
        });

        mLvDepartmentsDetail.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                String str = mDepartmentsDetailAdapter.getDepartmentsDetail(position);
                intent.putExtra(Extra.KData, str);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

}
