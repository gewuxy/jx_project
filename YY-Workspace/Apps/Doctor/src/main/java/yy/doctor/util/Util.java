package yy.doctor.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import lib.network.Network;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.ReflectUtil;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.util.BaseUtil;
import yy.doctor.App;
import yy.doctor.Extra.FileFrom;
import yy.doctor.R;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;
import yy.doctor.model.unitnum.File;
import yy.doctor.model.unitnum.File.TFile;
import yy.doctor.network.NetFactory.MeetParam;
import yy.doctor.ui.activity.data.DownloadFileActivityRouter;
import yy.doctor.ui.activity.CommonWebViewActivityRouter;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    public static void addBackIcon(NavBar n, final Activity act) {
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, CharSequence text, final Activity act) {
        n.addTextViewMid(text);
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, @StringRes int id, final Activity act) {
        n.addTextViewMid(id);
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
    }

    public static String convertUrl(String url) {
        return TextUtil.toUtf8(url.trim());
    }

    /**
     * 获取NavBar里需要的控件
     *
     * @param parent NavBar的控件
     * @param clz
     * @param <T>
     * @return
     */
    public static <T extends View> T getBarView(ViewGroup parent, Class<T> clz) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                getBarView((ViewGroup) childView, clz);
            } else {
                if (childView.getClass().isAssignableFrom(clz)) {
                    return (T) childView;
                }
            }
        }
        return ReflectUtil.newInst(clz);
    }

    /**
     * 获取输入框的文本
     *
     * @param et
     * @return
     */
    public static String getEtString(EditText et) {
        return et.getText().toString().trim();
    }

    /**
     * 检验是否是电话号码
     */
    public static boolean isMobileCN(CharSequence phone) {
        return RegexUtil.isMobileCN(phone.toString().replace(" ", ""));
    }

    /**
     * 密码允许输入的特殊符
     *
     * @return
     */
    public static String symbol() {
        String character = "^([A-Za-z_0-9]|-|×|÷|＝|%|√|°|′|″|\\{|\\}|\\(|\\)|\\[|\\]|\\.|\\||\\*|/|#|~|,|:|;|\\?|\"|‖|&|\\*|@|\\|\\^|,|\\$|–|…|'|=|\\+|!|>|<|\\.|-|—|_)+$";
        return character;
    }

    public static boolean noNetwork() {
        boolean b = !DeviceUtil.isNetworkEnabled();
        if (b) {
            App.showToast(Network.getConfig().getDisconnectToast());
        }
        return b;
    }

    public static String chooseToJson(@NonNull List<Topic> topics) {
        JSONArray arr = new JSONArray();

        for (Topic topic : topics) {
            String answer = topic.getString(TTopic.choice);
            if (TextUtil.isNotEmpty(answer)) {
                JSONObject o = new JSONObject();
                try {
                    o.put(MeetParam.KQuestionId, topic.getString(TTopic.id));
                    o.put(MeetParam.KAnswer, answer);
                } catch (JSONException e) {
                    YSLog.e("toJson", "chooseToJson", e);
                }
                arr.put(o);
            }
        }
        return arr.toString();
    }

    /**
     * 切换屏幕方向(1秒后设置回默认值)
     *
     * @param act
     * @param orientation
     */
    public static void changeOrientation(Activity act, int orientation) {
        act.setRequestedOrientation(orientation);
        Observable.just((Runnable) () ->
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)) // 设置回默认值
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribe(Runnable::run);
    }

    /**
     * 打开文件
     */
    public static void openFile(File file, @FileFrom int fromType, String cacheName) {
        long size = file.getLong(TFile.fileSize);
        String id = file.getString(TFile.id);
        String name;
        String url;
        String path;
        String fileType;
        if (fromType == FileFrom.unit_num) {
            path = CacheUtil.getUnitNumCacheDir(cacheName);
            name = file.getString(TFile.materialName);
            url = file.getString(TFile.materialUrl);
            fileType = file.getString(TFile.materialType);
        } else {
            path = CacheUtil.getMeetingCacheDir(cacheName);
            name = file.getString(TFile.name);
            url = file.getString(TFile.fileUrl);
            fileType = file.getString(TFile.fileType);
        }
        String htmlUrl = file.getString(TFile.htmlUrl);
        if (TextUtil.isEmpty(htmlUrl)) {
            DownloadFileActivityRouter.create()
                    .filePath(path)
                    .fileName(name)
                    .url(url)
                    .fileSuffix(fileType)
                    .fileSize(size)
                    .dataFileId(id)
                    .dataType(DataType.un_know)
                    .route(App.getContext());
        } else {
            CommonWebViewActivityRouter.create(file.getString(TFile.materialName), htmlUrl)
                    .fileId(id)
                    .type(fromType)
                    .route(App.getContext());
        }
    }

    public static boolean checkApkExist(String packageName) {
        if (TextUtil.isEmpty(packageName))
            return false;
        try {
            App.getContext().getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
