package yy.doctor.util;

import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.ex.TitleBarEx;
import lib.yy.util.BaseUtil;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

//    public static void addBackIcon(TitleBarEx titleBar, final Activity act) {
//        titleBar.addImageViewLeft(R.drawable.back_selector_light, new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                act.finish();
//            }
//        });
//    }

    public static void addMenuIcon(TitleBarEx titleBar, final MainActivity act) {
        titleBar.addImageViewLeft(R.mipmap.title_ic_menu, new OnClickListener() {

            @Override
            public void onClick(View v) {
                act.toggleMenu();
            }
        });
    }


}
