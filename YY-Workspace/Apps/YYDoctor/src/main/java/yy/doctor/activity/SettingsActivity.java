package yy.doctor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/12
 */
public class SettingsActivity extends BaseFormActivity {

    private RelativeLayout mAuto_load_apk;
    private TextView mExit_account,mWiFi_tx;

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
        return inflate(R.layout.activity_settings_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mAuto_load_apk=findView(R.id.layout_auto_load_apk);
        mExit_account=findView(R.id.exit_account);
        mWiFi_tx=findView(R.id.load_apk_iswifi_tx);

    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mAuto_load_apk.setOnClickListener(this);
        mExit_account.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id=v.getId();
        switch (id){
            case R.id.layout_auto_load_apk: {
                showDialog();
            }
            break;
            case R.id.exit_account: {
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
                Intent intent=new Intent(this,ClearCacheActivity.class);
                startActivity(intent);
            }
            break;
        }

    }

    private void showDialog(){

        final Dialog dialogLoad = new Dialog(this, R.style.dialog_two_tx);
        Window windowLoad = dialogLoad.getWindow();
        windowLoad.setContentView(R.layout.dialog_common);
        final TextView textWifi = (TextView) windowLoad.findViewById(R.id.dialog_tx_one);
        final TextView textNever = (TextView) windowLoad.findViewById(R.id.dialog_tx_two);
        textWifi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWiFi_tx.setText(textWifi.getText());
                if(dialogLoad != null && dialogLoad.isShowing()){
                    dialogLoad.dismiss();
                }
            }
        });
        textNever.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWiFi_tx.setText(textNever.getText());
                if(dialogLoad != null && dialogLoad.isShowing()){
                    dialogLoad.dismiss();
                }
            }
        });
        dialogLoad.show();

    }

}
