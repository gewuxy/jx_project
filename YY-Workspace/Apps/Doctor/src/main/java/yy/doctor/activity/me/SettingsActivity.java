package yy.doctor.activity.me;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.ui.other.NavBar;
import lib.ys.view.ToggleButton;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.activity.LoginActivity;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.BottomDialog.OnDialogItemClickListener;
import yy.doctor.dialog.CommonDialog;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.serv.LogoutServ;
import yy.doctor.util.Util;

/**
 * 设置页面
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class SettingsActivity extends BaseFormActivity {

    private static final int KColorNormal = Color.parseColor("#666666");
    private static final int KColorCancel = Color.parseColor("#01b557");

    private ToggleButton mTog;
    private TextView mTvExit;

    @IntDef({
            RelatedId.check_version,

            RelatedId.change_password,
            RelatedId.clear_img_cache,

            RelatedId.clear_sound_cache,
    })
    private @interface RelatedId {
        int check_version = 1;

        int change_password = 2;

        int clear_img_cache = 3;
        int clear_sound_cache = 4;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "设置", this);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content_text)
                .related(RelatedId.check_version)
                .name("检查版本更新")
                .build());

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
    protected View createHeaderView() {
        return inflate(R.layout.layout_settings_header);
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_settings_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTog = findView(R.id.settings_header_switcher);
        mTvExit = findView(R.id.settings_footer_tv_exit_account);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvExit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.settings_footer_tv_exit_account: {
                showDialogExit();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.check_version: {
                showToast("852");
            }
            break;
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

    private void showDialogExit() {

        final CommonDialog dialog = new CommonDialog(this);
        dialog.addItem("退出当前账号", new OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                startService(LogoutServ.class);

                SettingsActivity.this.notify(NotifyType.logout);
                startActivity(LoginActivity.class);
                finish();
            }
        });

        dialog.addItem("关闭YaYa", new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SettingsActivity.this.notify(NotifyType.logout);
                finish();
            }
        });
        dialog.show();
    }

    private void showDialogClearImgCache() {

        final List<String> data = new ArrayList<>();
        data.add("清理图片缓存");
        data.add("取消");

        final BottomDialog dialog = new BottomDialog(this, new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {

                if (position == 0) {
                    showToast("555");
                }
            }
        });
        for (int i = 0; i < data.size(); ++i) {
            if (i != (data.size() - 1)) {
                dialog.addItem(data.get(i), KColorNormal);
            } else {
                dialog.addItem(data.get(i), KColorCancel);
            }
        }
        dialog.show();
    }

    private void showDialogClearSoundCache() {

        final List<String> data = new ArrayList<>();
        data.add("清理声音缓存");
        data.add("取消");

        final BottomDialog dialog = new BottomDialog(this, new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {

                if (position == 0) {
                    showToast("666");
                }
            }
        });
        for (int i = 0; i < data.size(); ++i) {
            if (i != (data.size() - 1)) {
                dialog.addItem(data.get(i), KColorNormal);
            } else {
                dialog.addItem(data.get(i), KColorCancel);
            }
        }
        dialog.show();
    }

}
