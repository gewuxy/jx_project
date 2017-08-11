package lib.ys.action;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;

import java.io.File;
import java.util.List;

import lib.ys.AppEx;
import lib.ys.YSLog;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;

/**
 * @auther yuansui
 * @since 2017/6/30
 */

public class IntentAction {

    private static final String TAG = IntentAction.class.getSimpleName();

    /**
     * 邮件action
     *
     * @return
     */
    public static MailAction mail() {
        return new MailAction();
    }

    /**
     * 外部浏览器
     *
     * @return
     */
    public static BrowserAction browser() {
        return new BrowserAction();
    }

    /**
     * 安卓市场
     *
     * @return
     */
    public static MarketAction market() {
        return new MarketAction();
    }

    public static MapAction map() {
        return new MapAction();
    }

    public static AppAction app() {
        return new AppAction();
    }

    public static AnyAction any() {
        return new AnyAction();
    }

    public static WordAction word() {
        return new WordAction();
    }

    public static PptAction ppt() {
        return new PptAction();
    }

    public static ExcelAction excel() {
        return new ExcelAction();
    }

    public static AppSetupAction appSetup() {
        return new AppSetupAction();
    }

    abstract static public class BaseAction {
        protected boolean mCreateChooser;
        protected String mChooserTitle;
        protected String mAlert;

        abstract public void launch();

        public <T extends BaseAction> T createChooser(String title) {
            mCreateChooser = true;
            mChooserTitle = title;
            return (T) this;
        }

        public <T extends BaseAction> T alert(String a) {
            mAlert = a;
            return (T) this;
        }

        /**
         * 以选择器的方式打开
         *
         * @param intent
         */
        protected void chooserLaunch(Intent intent) {
            LaunchUtil.startActivity(AppEx.ct(), Intent.createChooser(intent, mChooserTitle));
        }

        /**
         * 正常方式打开, 有可能没有安装相关应用
         *
         * @param intent
         */
        protected void normalLaunch(Intent intent) {
            try {
                LaunchUtil.startActivity(AppEx.ct(), intent);
            } catch (Exception e) {
                YSLog.e(TAG, "normalLaunch", e);
                if (mAlert != null) {
                    AppEx.showToast(mAlert);
                }
            }
        }
    }

    /**
     * 邮件
     */
    public static class MailAction extends BaseAction {

        private String mAddress;
        private String mSubject;
        private int mSubjectId;
        private String mText;

        private MailAction() {
        }

        public MailAction address(String a) {
            mAddress = a;
            return this;
        }

        public MailAction subject(String s) {
            mSubject = s;
            return this;
        }

        public MailAction subject(@StringRes int id) {
            mSubjectId = id;
            return this;
        }

        public MailAction text(String t) {
            mText = t;
            return this;
        }

        @Override
        public void launch() {
            Intent intent = new Intent(Intent.ACTION_SEND)
                    .setType("plain/text") // don't use final define
                    .putExtra(Intent.EXTRA_EMAIL, new String[]{mAddress})
                    .putExtra(Intent.EXTRA_TEXT, mText);
            if (TextUtil.isEmpty(mSubject)) {
                intent.putExtra(Intent.EXTRA_SUBJECT, mSubjectId);
            } else {
                intent.putExtra(Intent.EXTRA_SUBJECT, mSubject);
            }

            if (mCreateChooser) {
                chooserLaunch(intent);
            } else {
                normalLaunch(intent);
            }
        }
    }

    /**
     * 外部浏览器
     */
    public static class BrowserAction extends BaseAction {

        private String mUrl;

        private BrowserAction() {
        }

        public BrowserAction url(String url) {
            mUrl = url;
            return this;
        }

        @Override
        public void launch() {
            if (mUrl == null) {
                YSLog.e(TAG, "launch", new IllegalStateException("url can not be null"));
                return;
            }

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(mUrl);
            intent.setData(uri);
            normalLaunch(intent);
        }
    }

    public static class MarketAction extends BaseAction {

        private MarketAction() {
        }

        @Override
        public void launch() {
            Intent intent = getIntent();
            // 检测intent是否可用
            PackageManager packageManager = AppEx.ct().getPackageManager();
            List<ResolveInfo> shareAppList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (shareAppList == null || shareAppList.size() == 0) {
                AppEx.showToast(mAlert);
            } else {
                normalLaunch(intent);
            }
        }

        public static Intent getIntent() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + AppEx.ct().getPackageName()));
            return intent;
        }
    }

    public static class MapAction extends BaseAction {

        private double mLatitude;
        private double mLongitude;
        private String mName;

        private MapAction() {
        }

        /**
         * 纬度
         *
         * @param latitude
         * @return
         */
        public MapAction latitude(double latitude) {
            mLatitude = latitude;
            return this;
        }

        /**
         * 经度
         */
        public MapAction longitude(double longitude) {
            mLongitude = longitude;
            return this;
        }

        /**
         * 地点名称
         *
         * @param name
         * @return
         */
        public MapAction name(String name) {
            mName = name;
            return this;
        }

        @Override
        public void launch() {
            StringBuffer buffer = new StringBuffer()
                    .append("geo:")
                    .append(mLatitude)
                    .append(",")
                    .append(mLongitude);

            if (TextUtil.isNotEmpty(mName)) {
                buffer.append("?q=")
                        .append(mName);
            }

            Uri mUri = Uri.parse(buffer.toString());
            Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
            normalLaunch(mIntent);
        }
    }

    /**
     * 指定打开App的场景, 如支付宝, 微信
     */
    public static class AppAction extends BaseAction {

        private String mUrl;

        private AppAction() {
        }

        public AppAction url(String url) {
            mUrl = url;
            return this;
        }

        @Override
        public void launch() {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
            normalLaunch(intent);
        }
    }

    public static class AnyAction extends BaseAction {

        private Intent mIntent;

        private AnyAction() {
        }

        public AnyAction intent(Intent i) {
            mIntent = i;
            return this;
        }

        @Override
        public void launch() {
            normalLaunch(mIntent);
        }
    }

    /**
     * 调用第三方打开Word文件
     */
    public static class WordAction extends BaseAction {

        private String mFilePath;

        private WordAction() {
        }

        public WordAction filePath(String filePath) {
            mFilePath = filePath;
            return this;
        }

        @Override
        public void launch() {
            Uri uri = Uri.fromFile(new File(mFilePath));
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setDataAndType(uri, "application/msword");
            normalLaunch(intent);
        }
    }

    /**
     * 调用第三方打开PPT文件
     */
    public static class PptAction extends BaseAction {

        private String mFilePath;

        private PptAction() {
        }

        public PptAction filePath(String filePath) {
            mFilePath = filePath;
            return this;
        }

        @Override
        public void launch() {
            Uri uri = Uri.fromFile(new File(mFilePath));
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setDataAndType(uri, "application/vnd.ms-powerpoint");
            normalLaunch(intent);
        }
    }

    /**
     * 调用第三方打开excel文件
     */
    public static class ExcelAction extends BaseAction {

        private String mFilePath;

        public ExcelAction() {
        }

        public ExcelAction filePath(String filePath) {
            mFilePath = filePath;
            return this;
        }

        @Override
        public void launch() {
            Uri uri = Uri.fromFile(new File(mFilePath));
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setDataAndType(uri, "application/vnd.ms-excel");
            normalLaunch(intent);
        }
    }

    public static class AppSetupAction extends BaseAction {

        @Override
        public void launch() {
            Uri uri = Uri.parse("package:" + AppEx.ct().getPackageName());
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
            normalLaunch(intent);
        }
    }
}
