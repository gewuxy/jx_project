package yy.doctor.activity.me;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.ex.NavBar;
import lib.ys.view.photoViewer.NetworkPhotoView;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 裁剪图片
 *
 * @author CaiXiang
 * @since 2017/5/2
 */
public class ClipImageActivity extends BaseActivity {

    private NetworkPhotoView mPv;

    private String mPath;

    @Override
    public void initData() {
        mPath = getIntent().getStringExtra(Extra.KData);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_clip_image;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "头像", this);
        bar.addTextViewRight("确定", new OnClickListener() {

            @Override
            public void onClick(View v) {

                showToast("885");
            }
        });

    }

    @Override
    public void findViews() {
        mPv = findView(R.id.clip_image_pv);
    }

    @Override
    public void setViews() {
        setBackgroundColor(Color.BLACK);
        mPv.storage(mPath).load();
    }

}
