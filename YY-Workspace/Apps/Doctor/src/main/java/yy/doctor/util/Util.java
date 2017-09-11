package yy.doctor.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import lib.network.Network;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.util.BaseUtil;
import yy.doctor.App;
import yy.doctor.R;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;
import yy.doctor.network.NetFactory.MeetParam;

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

    /**
     * 获取会议科室列表
     *
     * @return
     */
    public static List<String> getSections() {
        String[] sectionNames = ResLoader.getStringArray(R.array.sections);
        return Arrays.asList(sectionNames);
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
        return ReflectionUtil.newInst(clz);
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
     * @return
     */
    public static String symbol(){
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
}
