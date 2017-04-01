package yy.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import lib.ys.util.JsonUtil;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Test;
import yy.doctor.model.Test.ETest;

public class MainActivity extends BaseActivity {

    private TextView mTvAaaaa;

    public static void nav(Context context, String test) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.KTestAble, test);
        LaunchUtil.startActivity(context, intent);
    }

    @Override
    public void initData() {
        getIntent().getStringExtra(Extra.KTestAble);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initTitleBar() {
        getTitleBar().addTextViewMid("test");
        getTitleBar().addImageViewLeft(R.mipmap.ic_loading, new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void findViews() {
        mTvAaaaa = findView(R.id.tv);

        mTvAaaaa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void setViewsValue() {

        setOnClickListener(mTvAaaaa);
        setOnClickListener(R.id.tv);

        Test t = JsonUtil.getEV(Test.class, new JSONObject());
        int count = t.getInt(ETest.count);
        for (int i = 0; i < count; ++i) {

        }

        mTvAaaaa.setText(t.getString(ETest.count));

        Profile t2 = t.getEv(ETest.test);
        List<Profile> l = t.getList(ETest.list);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv: {

            }
            break;
        }
    }

}
