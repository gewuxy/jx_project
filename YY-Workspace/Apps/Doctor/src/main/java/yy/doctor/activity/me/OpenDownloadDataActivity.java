package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 打开下载资料
 *
 * @author CaiXiang
 * @since 2017/5/17
 */

public class OpenDownloadDataActivity extends BaseActivity {

    private ImageView mIv;

    private String mName;
    private String mUrl;
    private String mType;
    private String mNum;

    public static void nav(Context context, String name, String url, String type, String num) {
        Intent i = new Intent(context, DownloadDataActivity.class)
                .putExtra(Extra.KName, name)
                .putExtra(Extra.KData, url)
                .putExtra(Extra.KType, type)
                .putExtra(Extra.KNum, num);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mName = getIntent().getStringExtra(Extra.KName);
        mUrl = getIntent().getStringExtra(Extra.KData);
        mType = getIntent().getStringExtra(Extra.KType);
        mNum = getIntent().getStringExtra(Extra.KNum);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_open_download_data;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "资料", this);
    }

    @Override
    public void findViews() {

        mIv = findView(R.id.open_download_data_ic);

    }

    @Override
    public void setViews() {

        mIv.setImageResource(R.mipmap.open_data_ic_pdf);
        setOnClickListener(R.id.open_download_data_tv_btn);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.open_download_data_tv_btn) {
            Intent intent = getPdfFileIntent("");
            startActivity(intent);
        }

    }

    /**
     * 获取一个用于打开Word文件的intent
     * @param param
     * @return
     */
    public Intent getWordFileIntent(String param) {
        Intent intent = new Intent("Intent.ACTION_VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    /**
     * 获取一个用于打开PDF文件的intent
     * @param param
     * @return
     */
    public Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * 获取一个用于打开PPT文件的intent
     * @param param
     * @return
     */
    public static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

}
