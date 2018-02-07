package jx.csp.ui.activity.main;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.adapter.main.ChoicePhotoAdapter;
import jx.csp.constant.Constants;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.main.photo.ChoiceCamera;
import jx.csp.model.main.photo.ChoicePhoto;
import jx.csp.model.main.photo.IUpload;
import jx.csp.ui.activity.edit.CreateMeetActivityRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.ys.fitter.Fitter;
import lib.ys.model.FileSuffix;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;

/**
 * 选择照片(新建讲本)界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/31
 */
@Route
public class ChoicePhotoActivity extends BasePhotoActivity<IUpload, ChoicePhotoAdapter> {

    @Arg(opt = true)
    ArrayList<String> mPaths;

    private final int KPerPhoto = 0;
    private final int KPerCamera = 1;

    private final int KReqCamera = 100;
    private final int KReqPhoto = 101;

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

        ItemTouchHelper helper = new ItemTouchHelper(new HelperImpl());
        helper.attachToRecyclerView(getScrollableView());

        if (mPaths != null && !mPaths.isEmpty()) {
            ChoicePhoto photo;
            for (String path : mPaths) {
                photo = new ChoicePhoto();
                photo.put(ChoicePhoto.TChoicePhoto.path, path);
                addItem(photo);
            }
            photoChange(mPaths.size());
        }
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
                if (checkPermission(KPerCamera, Permission.camera)) {
                    mPhotoPath = CacheUtil.getUploadCacheDir() + "photo" + System.currentTimeMillis() + FileSuffix.jpg;
                    PhotoUtil.fromCamera(ChoicePhotoActivity.this, mPhotoPath, KReqCamera);
                }
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_tv_bottom_left: {
                if (checkPermission(KPerPhoto, Permission.storage)) {
                    int canSelect = Constants.KPhotoMax - getPhotoSize();
                    if (canSelect > 0) {
                        PhotoActivityRouter.create()
                                .maxSelect(canSelect)
                                .route(ChoicePhotoActivity.this, KReqPhoto);
                    } else {
                        showToast(String.format(getString(R.string.upload_photo_max), Constants.KPhotoMax));
                    }
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
                CreateMeetActivityRouter.create()
                        .picture(paths)
                        .previewUrl(paths.get(0))
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
        photoChange(size);
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    /**
     * 选择图片后的改变
     *
     * @param size 选择的图片大小
     */
    private void photoChange(int size) {
        getRightButton().setEnabled(size > 0);
        if (size > 0) {
            getLeftButton().setText(getString(R.string.upload_photo) + "(" + size + "/" + Constants.KPhotoMax + ")");
        } else {
            setLeftText(R.string.upload_photo);
        }
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
     * 退出提示
     */
    private void exitDialog() {
        CommonDialog2 d = new CommonDialog2(ChoicePhotoActivity.this);
        d.setHint(R.string.exit_edit);
        d.addButton(R.string.cancel, R.color.text_404356, null);
        d.addButton(R.string.confirm, R.color.text_333, l -> finish());
        d.show();
    }

    @Override
    public void onPermissionResult(int code, int result) {
        if (code == KPerCamera) {
            switch (result) {
                case PermissionResult.granted: {
                    mPhotoPath = CacheUtil.getUploadCacheDir() + "photo" + System.currentTimeMillis() + FileSuffix.jpg;
                    PhotoUtil.fromCamera(ChoicePhotoActivity.this, mPhotoPath, KReqCamera);
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    UISetter.cameraNoPermission(ChoicePhotoActivity.this);
                }
                break;
            }
        } else if (code == KPerPhoto) {
            switch (result) {
                case PermissionResult.granted: {
                    getLeftButton().performClick();
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    UISetter.photoNoPermission(ChoicePhotoActivity.this);
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == KReqPhoto && data != null) {
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
        if (requestCode == KReqCamera) {
            ChoicePhoto photo = new ChoicePhoto();
            photo.put(ChoicePhoto.TChoicePhoto.path, mPhotoPath);
            addItem(photo);
            invalidate();
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.finish_editor_meet) {
            finish();
        }
    }

    /**
     * RecyclerView拖拽的实现
     */
    private class HelperImpl extends ItemTouchHelper.Callback {

        private RecyclerView.ViewHolder lastDragViewHolder;

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            // 当前控件的处理
            if (viewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                // ACTION_STATE_DRAG 拖拽,选中
                lastDragViewHolder = viewHolder;
                viewHolder.itemView.setAlpha(0.6f);
                viewHolder.itemView.setAlpha(0.6f);
            }
            // 上一个控件的处理
            if (lastDragViewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                // ACTION_STATE_IDLE 松开
                lastDragViewHolder.itemView.setAlpha(1);
                lastDragViewHolder.itemView.setAlpha(1);
                lastDragViewHolder = null;
            }
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
            if (position == getCount() - 1) {
                // 不拖动
                return 0;
            }
            return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int currentPosition = viewHolder.getAdapterPosition(); // 拖动ViewHolder的position
            int targetPosition = target.getAdapterPosition(); // 目标ViewHolder的position
            if (targetPosition == getCount() - 1) {
                // false表示不交换
                return false;
            }
            // 分别把中间所有的item的位置重新交换
            if (currentPosition < targetPosition) {
                for (int i = currentPosition; i < targetPosition; i++) {
                    Collections.swap(getData(), i, i + 1);
                }
            } else {
                for (int i = currentPosition; i > targetPosition; i--) {
                    Collections.swap(getData(), i, i - 1);
                }
            }
            getAdapter().notifyItemMoved(currentPosition, targetPosition);
            // true表示交换
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }
}
