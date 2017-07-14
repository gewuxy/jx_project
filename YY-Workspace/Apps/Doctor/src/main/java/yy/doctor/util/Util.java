package yy.doctor.util;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.util.BaseUtil;
import yy.doctor.Constants;
import yy.doctor.R;
import yy.doctor.model.Pcd;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    public static void addBackIcon(NavBar n, final Activity act) {
        n.addBackIcon(R.mipmap.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, CharSequence text, final Activity act) {
        n.addTextViewMid(text);
        n.addBackIcon(R.mipmap.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, @StringRes int id, final Activity act) {
        n.addTextViewMid(id);
        n.addBackIcon(R.mipmap.nav_bar_ic_back, act);
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
     * 根据省市区生成展示的字符串
     *
     * @param p
     * @param c
     * @param d
     * @return
     */
    public static String generatePcd(String p, String c, String d) {
        StringBuffer b = new StringBuffer()
                .append(p)
                .append(Pcd.KSplit)
                .append(c);
        if (TextUtil.isNotEmpty(d)) {
            b.append(Pcd.KSplit).append(d);
        }
        return b.toString();
    }

    public static String generatePcd(String[] pcd) {
        if (pcd == null || pcd.length < Pcd.KMaxCount) {
            return Constants.KEmptyValue;
        }
        return generatePcd(pcd[Pcd.KProvince], pcd[Pcd.KCity], pcd[Pcd.KDistrict]);
    }

    /**
     * 获取NavBar里需要的控件
     * @param parent NavBar的控件
     * @param clz
     * @param <T>
     * @return
     */
    public static  <T extends View> T getBarView(ViewGroup parent, Class<T> clz) {
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
}
