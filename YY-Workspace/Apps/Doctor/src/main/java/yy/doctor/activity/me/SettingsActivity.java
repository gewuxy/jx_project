package yy.doctor.activity.me;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.dialog.CommonDialog;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/12
 */
public class SettingsActivity extends BaseFormActivity {

    private RelativeLayout mLayoutDownload;
    private TextView mTvLoadApkCondition;
    private TextView mTvExit;

    private CommonDialog mDialogAutoDownload;
    private CommonDialog mDialogExit;

    @IntDef({
            RelatedId.binding_sine,
            RelatedId.change_password,
            RelatedId.clear_cache,
    })
    private @interface RelatedId {
        int binding_sine = 0;
        int change_password = 1;
        int clear_cache = 2;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.app_bg)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .related(RelatedId.binding_sine)
                .name("新浪微博绑定")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .related(RelatedId.change_password)
                .name("修改密码")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content)
                .related(RelatedId.clear_cache)
                .name("清理缓存")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.divider_large)
                .backgroundRes(R.color.app_bg)
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());
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
                mDialogAutoDownload.show();
            }
            break;
            case R.id.settings_footer_tv_exit_account: {
                mDialogExit.show();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedid = getItem(position).getInt(TFormElem.related);
        switch (relatedid) {
            case RelatedId.binding_sine: {
                showToast("0");
            }
            break;
            case RelatedId.change_password: {
                showToast("1");
            }
            break;
            case RelatedId.clear_cache: {
                Intent intent = new Intent(this, ClearCacheActivity.class);
                startActivity(intent);
            }
            break;
        }

    }

    private void initDialogAutoDownload() {

        mDialogAutoDownload = new CommonDialog(this);
        mDialogAutoDownload.addItem("仅在WiFi", new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvLoadApkCondition.setText("仅在WiFi");
                if (mDialogAutoDownload != null && mDialogAutoDownload.isShowing()) {
                    mDialogAutoDownload.dismiss();
                }
            }
        });
        mDialogAutoDownload.addItem("从不", new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvLoadApkCondition.setText("从不");
                if (mDialogAutoDownload != null && mDialogAutoDownload.isShowing()) {
                    mDialogAutoDownload.dismiss();
                }
            }
        });

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

}
