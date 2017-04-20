package yy.doctor.activity.me;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.dialog.ClearCacheDialog;
import yy.doctor.dialog.CommonDialog;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

/**
 * 设置页面
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class SettingsActivity extends BaseFormActivity {

    private RelativeLayout mLayoutDownload;
    private TextView mTvLoadApkCondition;
    private TextView mTvExit;

    @IntDef({
            RelatedId.change_password,
            RelatedId.clear_img_cache,
            RelatedId.clear_sound_cache,
    })
    private @interface RelatedId {
        int change_password = 1;
        int clear_img_cache = 2;
        int clear_sound_cache = 3;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.content)
                .related(RelatedId.change_password)
                .name("修改密码")
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.content)
                .name("清理图片缓存")
                .related(RelatedId.clear_img_cache)
                .text("88M")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .name("清理声音缓存")
                .related(RelatedId.clear_sound_cache)
                .text("66M")
                .build());

        addItem(new Builder(FormType.divider_large).build());

    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_settings_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutDownload = findView(R.id.settings_footer_layout_download);
        mTvLoadApkCondition = findView(R.id.settings_footer_tv_download_conditiom);
        mTvExit = findView(R.id.settings_footer_tv_exit_account);

    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mLayoutDownload.setOnClickListener(this);
        mTvExit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.settings_footer_layout_download: {
                showDialogAutoDownload();
            }
            break;
            case R.id.settings_footer_tv_exit_account: {
                showDialogExit();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedid = getItem(position).getInt(TFormElem.related);
        switch (relatedid) {
            case RelatedId.change_password: {
                startActivity(ChangePwdActivity.class);
            }
            break;
            case RelatedId.clear_img_cache: {
                showDialogClearImgCache();
            }
            break;
            case RelatedId.clear_sound_cache: {
                showDialogClearSoundCache();
            }
            break;
        }

    }

    private void showDialogAutoDownload() {

        final CommonDialog dialog = new CommonDialog(this);
        dialog.addItem("仅在WiFi", new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvLoadApkCondition.setText("仅在WiFi");
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.addItem("从不", new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvLoadApkCondition.setText("从不");
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void showDialogExit() {

        final CommonDialog dialog = new CommonDialog(this);
        dialog.addItem("退出当前账号", new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.addItem("关闭YaYa", new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }


    int color_normal = Color.parseColor("#666666");
    int color_cancle = Color.parseColor("#01b557");

    private void showDialogClearImgCache() {

        final ClearCacheDialog dialog = new ClearCacheDialog(this);
        dialog.addItem("清理图片缓存", color_normal, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.addItem("取消", color_cancle, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void showDialogClearSoundCache() {

        final ClearCacheDialog dialog = new ClearCacheDialog(this);
        dialog.addItem("清理声音缓存", color_normal, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.addItem("取消", color_cancle, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

}
