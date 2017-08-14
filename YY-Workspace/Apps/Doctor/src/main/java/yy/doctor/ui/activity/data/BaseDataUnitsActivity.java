package yy.doctor.ui.activity.data;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import inject.annotation.router.Arg;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVPActivity;
import yy.doctor.R;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag;

/**
 * @auther Huoxuyu
 * @since 2017/8/2
 */
abstract public class BaseDataUnitsActivity extends BaseVPActivity {

    private static final String KSeparate = " > ";

    @Arg(optional = true)
    String mId;

    @Arg(optional = true)
    String mFileName;

    @Arg(optional = true)
    boolean mLeaf;

    @Arg(optional = true)
    String mPath;

    private EditText mEtPath;

    @CallSuper
    @Override
    public void initData() {
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

    abstract protected BaseDataUnitsFrag createFrag();
}
