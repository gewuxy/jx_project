package jx.doctor.ui.activity.data;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import inject.annotation.router.Arg;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVpActivity;
import jx.doctor.R;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag;

/**
 * 数据中心
 *
 * @auther Huoxuyu
 * @since 2017/8/2
 */
abstract public class BaseDataUnitsActivity extends BaseVpActivity {

    private static final String KSeparate = " > ";

    @Arg(opt = true)
    String mId;

    @Arg(opt = true)
    String mFileName;

    @Arg(opt = true)
    boolean mLeaf;

    @Arg(opt = true)
    String mPath;

    private EditText mEtPath;

    @CallSuper
    @Override
    public void initData(Bundle state) {
        add(createFrag());
    }

    @Override
    public void findViews() {
        super.findViews();

        mEtPath = findView(R.id.data_path_et);
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtPath.setText(mPath);
    }

    public String buildPath(String newPath) {
        return mPath + KSeparate + newPath;
    }

    @Nullable
    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_data_path);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.data_finish) {
            finish();
        }
    }

    @Nullable
    @Override
    public int getContentHeaderViewId() {
        return 0;
    }

    abstract protected BaseDataUnitsFrag createFrag();
}
