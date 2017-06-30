package lib.ys.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.StringRes;

import java.util.List;

import lib.ys.AppEx;
import lib.ys.YSLog;

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

        @Override
        public void launch() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + AppEx.ct().getPackageName()));
            // 检测intent是否可用
            PackageManager packageManager = AppEx.ct().getPackageManager();
            List<ResolveInfo> shareAppList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (shareAppList == null || shareAppList.size() == 0) {
                AppEx.showToast(mAlert);
            } else {
                normalLaunch(intent);
            }
        }
    }

}
