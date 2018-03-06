package jx.csp.ui.activity.edit;

import android.content.Intent;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jx.csp.R;
import jx.csp.adapter.main.PhotoAdapter;
import jx.csp.constant.Constants;
import jx.csp.model.main.Photo;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;

/**
 * 相册界面
 *
 * @auther : GuoXuan
 * @since : 2018/2/2
 */
@Route
public class PhotoActivity extends BasePhotoActivity<Photo, PhotoAdapter> implements
        MultiAdapterEx.OnAdapterClickListener {

    @Arg(opt = true, defaultInt = Constants.KPhotoMax)
    int mMaxSelect;

    @Arg(opt = true)
    boolean mFromMain;

    private int mSelectNum; // 被选择数

    @Override
    public void initData() {
        mSelectNum = 0;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.default_ic_close, l -> finish());
        bar.addTextViewMid(R.string.photo_film);
        Util.addDivider(bar);
    }

    @Override
    public void setViews() {
        super.setViews();

        getRightButton().setEnabled(false);
        Observable.fromCallable(() -> {
            List<String> photos = new ArrayList<>();
            String path = CacheUtil.getUploadCacheDir();
            File file = new File(path);
            if (file.exists()) {
                addFile(photos, file);
            }
            photos.addAll(PhotoUtil.getPhotos(PhotoActivity.this));
            return photos;
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photos -> {
                    for (String photo : photos) {
                        Photo p = new Photo();
                        p.put(Photo.TPhoto.path, photo);
                        addItem(p);
                    }
                    invalidate();
                });
        setLeftText(R.string.preview);
        setRightText(R.string.finish);
    }

    private void addFile(List<String> photos, File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFile(photos, f);
            }
        } else {
            if (file.getName().contains("jpg")) {
                photos.add(file.getAbsolutePath());
            }
        }
    }

    @Override
    protected int getSpanCount() {
        return PhotoAdapter.KSpanCount;
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.photo_iv_pic: {
                // 列表的UI
                boolean select = !getItem(position).getBoolean(Photo.TPhoto.choice, false);
                if (select) {
                    // 记录前是Max个不做操作
                    if (mSelectNum == mMaxSelect) {
                        return;
                    }
                    mSelectNum++;
                    // 记录完是Max个盖图层
                    if (mSelectNum == mMaxSelect) {
                        for (Photo photo : getData()) {
                            if (!photo.getBoolean(Photo.TPhoto.choice, false)) {
                                photo.put(Photo.TPhoto.cover, true);
                            }
                        }
                    }
                } else {
                    if (mSelectNum == mMaxSelect) {
                        for (Photo photo : getData()) {
                            photo.put(Photo.TPhoto.cover, false);
                        }
                    }
                    mSelectNum--;
                }
                getItem(position).put(Photo.TPhoto.choice, select);
                getItem(position).put(Photo.TPhoto.cover, false);
                invalidate();
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_tv_bottom_left: {
                ArrayList<String> photos = getSelectPhoto();
                if (!photos.isEmpty()) {
                    PreviewPhotoActivityRouter.create(photos)
                            .maxSelect(mMaxSelect)
                            .route(PhotoActivity.this, 0);
                }
            }
            break;
            case R.id.photo_tv_bottom_right: {
                if (mFromMain) {
                    ChoicePhotoActivityRouter.create().paths(getSelectPhoto()).route(PhotoActivity.this);
                    finish();
                } else {
                    Intent i = new Intent().putExtra(Constants.KData, getSelectPhoto());
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
            break;
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        // 预览按钮
        getLeftButton().setSelected(mSelectNum > 0);
        // 完成按钮
        String finishText = getString(R.string.finish);
        getRightButton().setText(mSelectNum > 0 ? finishText.concat("(").concat(String.valueOf(mSelectNum)).concat(")") : finishText);
        getRightButton().setEnabled(mSelectNum > 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            getRightButton().performClick();
        }
    }

    public ArrayList<String> getSelectPhoto() {
        ArrayList<String> photos = new ArrayList<>();
        for (Photo photo : getData()) {
            if (photo.getBoolean(Photo.TPhoto.choice, false)) {
                photos.add(photo.getString(Photo.TPhoto.path));
            }
        }
        return photos;
    }
}
