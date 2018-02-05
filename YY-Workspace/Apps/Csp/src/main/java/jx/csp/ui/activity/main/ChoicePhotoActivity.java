package jx.csp.ui.activity.main;

import android.content.Intent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

import jx.csp.R;
import jx.csp.adapter.main.ChoicePhotoAdapter;
import jx.csp.constant.Constants;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.main.photo.ChoiceCamera;
import jx.csp.model.main.photo.ChoicePhoto;
import jx.csp.model.main.photo.IUpload;
import jx.csp.ui.activity.share.EditorActivityRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.ys.fitter.Fitter;
import lib.ys.model.FileSuffix;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;

/**
 * 选择照片(新建讲本)界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/31
 */
public class ChoicePhotoActivity extends BasePhotoActivity<IUpload, ChoicePhotoAdapter> {

    private final int KCamera = 100;
    private final int KPhoto = 101;

    private String mPhotoPath;

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.nav_bar_ic_back, l -> exitDialog());
        bar.addTextViewMid(R.string.add_meet);
        Util.addDivider(bar);
    }

    @Override
    public void setViews() {
        super.setViews();

        setLeftText(R.string.upload_photo);
        setRightText(R.string.make_meet);

        getLeftButton().setSelected(true);
        getRightButton().setEnabled(false);

        getScrollableView().setPadding(Fitter.dimen(R.dimen.margin),
                getScrollableView().getPaddingTop(),
                getScrollableView().getRight(),
                getScrollableView().getBottom());

        addItem(new ChoiceCamera());
    }

    @Override
    protected int getSpanCount() {
        return ChoicePhotoAdapter.KSpanCount;
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.choice_photo_iv_delete: {
                remove(position);
                invalidate();
            }
            break;
            case R.id.choice_photo_layout_add_camera: {
                mPhotoPath = CacheUtil.getUploadCacheDir() + "photo" + System.currentTimeMillis() + FileSuffix.jpg;
                PhotoUtil.fromCamera(ChoicePhotoActivity.this, mPhotoPath, KCamera);
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_tv_bottom_left: {
                int canSelect = Constants.KPhotoMax - getPhotoSize();
                if (canSelect > 0) {
                    PhotoActivityRouter.create()
                            .maxSelect(canSelect)
                            .route(ChoicePhotoActivity.this, KPhoto);
                } else {
                    showToast(String.format(getString(R.string.upload_photo_max), Constants.KPhotoMax));
                }
            }
            break;
            case R.id.photo_tv_bottom_right: {
                ArrayList<String> paths = new ArrayList<>();
                ChoicePhoto c;
                for (IUpload upload : getData()) {
                    if (upload instanceof ChoicePhoto) {
                        c = (ChoicePhoto) upload;
                        paths.add(c.getString(ChoicePhoto.TChoicePhoto.path));
                    }
                }
                EditorActivityRouter.create(false)
                        .picture(paths)
                        .route(ChoicePhotoActivity.this);
            }
            break;
        }
    }

    @Override
    public void invalidate() {
        if (getData() != null && getData().size() > 0) {
            for (IUpload upload : getData()) {
                if (upload instanceof ChoiceCamera) {
                    remove(upload);
                    break;
                }
            }
        }
        if (getData() == null || getData().size() < Constants.KPhotoMax) {
            addItem(new ChoiceCamera());
        }

        super.invalidate();

        int size = getPhotoSize();
        getRightButton().setEnabled(size > 0);
        if (size > 0) {
            getLeftButton().setText(getString(R.string.upload_photo) + "(" + size + "/" + Constants.KPhotoMax + ")");
        } else {
            setLeftText(R.string.upload_photo);
        }
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    /**
     * 除去摄像头的Item
     *
     * @return 照片的size
     */
    private int getPhotoSize() {
        int num = 0;
        if (getData() == null || getData().isEmpty()) {
            return num;
        }
        for (IUpload upload : getData()) {
            if (upload instanceof ChoicePhoto) {
                num++;
            }
        }
        return num;
    }

    /**
     * 退出编辑的提示
     */
    private void exitDialog() {
        CommonDialog2 d = new CommonDialog2(ChoicePhotoActivity.this);
        d.setHint(R.string.exit_edit);
        d.addButton(R.string.cancel, R.color.text_404356, null);
        d.addButton(R.string.confirm, R.color.text_333, l -> finish());
        d.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == KPhoto && data != null) {
            Serializable serializable = data.getSerializableExtra(Constants.KData);
            if (serializable instanceof ArrayList) {
                ArrayList<String> photos = (ArrayList<String>) serializable;
                if (photos.isEmpty()) {
                    return;
                }
                ChoicePhoto choicePhoto;
                for (String photo : photos) {
                    choicePhoto = new ChoicePhoto();
                    choicePhoto.put(ChoicePhoto.TChoicePhoto.path, photo);
                    addItem(choicePhoto);
                }
                invalidate();
            }
        }
        if (requestCode == KCamera) {
            ChoicePhoto photo = new ChoicePhoto();
            photo.put(ChoicePhoto.TChoicePhoto.path, mPhotoPath);
            addItem(photo);
            invalidate();
        }
    }

}
