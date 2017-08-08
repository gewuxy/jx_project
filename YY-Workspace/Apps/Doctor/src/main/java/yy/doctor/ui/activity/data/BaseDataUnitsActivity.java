package yy.doctor.ui.activity.data;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import lib.processor.annotation.Extra;
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

    @Extra(optional = true)
    String mId;

    @Extra(optional = true)
    String mFileName;

    @Extra(optional = true)
    boolean mLeaf;

    @Extra(optional = true)
    String mPath;

    private EditText mEtPath;

    @CallSuper
    @Override
    public void initData() {
        BaseDataUnitsFrag frag = createFrag();
        Bundle bundle = new Bundle();
        bundle.putString(yy.doctor.Extra.KId, mId);
        bundle.putBoolean(yy.doctor.Extra.KLeaf, mLeaf);
        frag.setArguments(bundle);

        add(frag);

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
