package yy.doctor.activity.me;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lib.ys.LogMgr;
import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.view.ToggleButton;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.LoginActivity;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.BottomDialog.OnDialogItemClickListener;
import yy.doctor.dialog.CommonDialog;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.serv.CommonServ;
import yy.doctor.util.CacheUtil;
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

    private String mImgSize;
    private String mSoundSize;

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

        try {
            mImgSize = (FileUtil.getFolderSize(new File(CacheUtil.getBmpCacheDir())) / 1024) / 1024 + "M";
            mImgSize += (FileUtil.getFolderSize(new File(CacheUtil.getUploadCacheDir())) / 1024) / 1024 + "M";

            LogMgr.d(TAG, " mImgSize = " + mImgSize);
        } catch (Exception e) {
            e.printStackTrace();
            mImgSize = "0M";
            LogMgr.d(TAG, " error mImgSize = " + mImgSize);
        }
        try {
            mSoundSize = (FileUtil.getFolderSize(new File(CacheUtil.getMeetingSoundCacheDir())) / 1024) / 1024 + "M";
            LogMgr.d(TAG, " mSoundSize = " + mSoundSize);
        } catch (Exception e) {
            e.printStackTrace();
            mSoundSize = "0M";
            LogMgr.d(TAG, " error  mSoundSize = " + mImgSize);
        }

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
                .text(mImgSize)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.content)
                .name("清理声音缓存")
                .related(RelatedId.clear_sound_cache)
                .text(mSoundSize)
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

        mTog.setToggleState(true);
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
                showToast("已是最新版本");
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

                Intent intent = new Intent(SettingsActivity.this, CommonServ.class);
                intent.putExtra(Extra.KType, Extra.KLogout);
                startService(intent);

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

                    Observable.just("888")
                            .doOnSubscribe(disposable -> FileUtil.delFolder(CacheUtil.getBmpCacheDir()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> {
                                getRelatedItem(RelatedId.clear_img_cache).put(TFormElem.text, "0M");
                                refreshRelatedItem(RelatedId.clear_img_cache);
                                showToast("图片缓存清理完毕");
                            });
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
                    Observable.just("666")
                            .doOnSubscribe(disposable -> FileUtil.delFolder(CacheUtil.getMeetingSoundCacheDir()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> {
                                getRelatedItem(RelatedId.clear_sound_cache).put(TFormElem.text, "0M");
                                refreshRelatedItem(RelatedId.clear_sound_cache);
                                showToast("声音缓存清理完毕");
                            });
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
