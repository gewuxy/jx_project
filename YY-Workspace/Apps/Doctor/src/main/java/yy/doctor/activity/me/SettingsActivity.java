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

        addItem(new Builder(FormType.content_no_img_tx)
                .related(RelatedId.binding_sine)
                .name("新浪微博绑定")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img_tx)
                .related(RelatedId.change_password)
                .name("修改密码")
                .build());

        addItem(new Builder(FormType.divider)
                .backgroundRes(R.color.divider)
                .build());

        addItem(new Builder(FormType.content_no_img_tx)
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
                showDialog();
            }
            break;
            case R.id.settings_footer_tv_exit_account: {
                showToast("77");
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

    private void showDialog() {

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

}
