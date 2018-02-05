package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import jx.csp.R;
import lib.jx.dialog.BaseDialog;
import lib.ys.network.image.NetworkImageView;

/**
 * 编辑页面的预览
 *
 * @auther HuoXuYu
 * @since 2018/2/2
 */

public class PreviewDialog extends BaseDialog {

    private NetworkImageView mIvTheme;
    private NetworkImageView mIvPhoto;

    public PreviewDialog(Context context, String theme, String photo) {
        super(context);

        mIvTheme.url(theme).load();
        if (photo.startsWith("http")) {
            mIvPhoto.url(photo).load();
        } else {
            mIvPhoto.storage(photo).load();
        }
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_preview;
    }

    @Override
    public void findViews() {
        mIvTheme = findView(R.id.preview_iv_theme);
        mIvPhoto = findView(R.id.preview_iv_photo);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.preview_iv_cancel);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_iv_cancel: {
                dismiss();
            }
            break;
        }
    }
}
