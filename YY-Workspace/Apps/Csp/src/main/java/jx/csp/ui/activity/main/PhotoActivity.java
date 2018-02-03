package jx.csp.ui.activity.main;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.adapter.main.PhotoAdapter;
import jx.csp.constant.Constants;
import jx.csp.model.main.Photo;
import jx.csp.util.Util;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.ui.activity.list.RecyclerActivityEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;

/**
 * 相册界面
 *
 * @auther : GuoXuan
 * @since : 2018/2/2
 */
public class PhotoActivity extends RecyclerActivityEx<Photo, PhotoAdapter> implements
        MultiAdapterEx.OnAdapterClickListener {

    public static final int KSelectMax = 9; // 选择的最大数
    private int mSelectNum; // 被选择数

    private TextView mTvPreview;
    private TextView mTvFinish;

    @Override
    public int getContentViewId() {
        return R.layout.activity_photo;
    }

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
    protected RecyclerView.LayoutManager initLayoutManager() {
        return new GridLayoutManager(PhotoActivity.this, PhotoAdapter.KSpanCount);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvPreview = findView(R.id.photo_tv_preview);
        mTvFinish = findView(R.id.photo_tv_finish);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener(this);
        setOnClickListener(mTvPreview);
        setOnClickListener(mTvFinish);
        mTvFinish.setEnabled(false);

        // FIXME: 待整理
        Util.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                List<String> photos = PhotoUtil.getPhotos(PhotoActivity.this);
                Util.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        for (String photo : photos) {
                            Photo p = new Photo();
                            p.put(Photo.TPhoto.path, photo);
                            addItem(p);
                        }
                        invalidate();
                    }
                });
            }
        });
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.photo_iv_pic: {
                // 列表的UI
                boolean select = !getItem(position).getBoolean(Photo.TPhoto.choice, false);
                if (select) {
                    // 记录前是Max个不做操作
                    if (mSelectNum == KSelectMax) {
                        return;
                    }
                    mSelectNum++;
                    // 记录完是Max个盖图层
                    if (mSelectNum == KSelectMax) {
                        for (Photo photo : getData()) {
                            if (!photo.getBoolean(Photo.TPhoto.choice, false)) {
                                photo.put(Photo.TPhoto.cover, true);
                            }
                        }
                    }
                } else {
                    if (mSelectNum == KSelectMax) {
                        for (Photo photo : getData()) {
                            photo.put(Photo.TPhoto.cover, false);
                        }
                    }
                    mSelectNum--;
                }
                getItem(position).put(Photo.TPhoto.choice, select);
                getItem(position).put(Photo.TPhoto.cover, false);
                invalidate();
                // 预览按钮
                mTvPreview.setSelected(mSelectNum > 0);
                // 完成按钮
                String finishText = getString(R.string.finish);
                mTvFinish.setText(mSelectNum > 0 ? finishText.concat("(").concat(String.valueOf(mSelectNum)).concat(")") : finishText);
                mTvFinish.setEnabled(mSelectNum > 0);
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_tv_preview: {
                ArrayList<String> photos = getSelectPhoto();
                if (!photos.isEmpty()) {
                    PreviewPhotoActivityRouter.create(photos).route(PhotoActivity.this, 0);
                }
            }
            break;
            case R.id.photo_tv_finish: {
                finish();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent i = new Intent().putExtra(Constants.KData, getSelectPhoto());
        setResult(RESULT_OK, i);
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
