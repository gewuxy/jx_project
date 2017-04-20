package yy.doctor.util;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import lib.ys.ex.TitleBarEx;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.view.ViewUtil;
import lib.yy.util.BaseUtil;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    public static void addBackIcon(TitleBarEx titleBar, final Activity act) {
        titleBar.addImageViewLeft(R.mipmap.ic_back, new OnClickListener() {

            @Override
            public void onClick(View v) {
                act.finish();
            }
        });
    }

    public static void addBackIcon(TitleBarEx titleBar, CharSequence text, final Activity act) {
        View v = ViewUtil.inflateLayout(R.layout.layout_title_bar_back);
        LayoutFitter.fit(v);

        TextView tv = (TextView) v.findViewById(R.id.title_bar_left_tv);
        tv.setText(text);

        titleBar.addViewLeft(v, new OnClickListener() {

            @Override
            public void onClick(View v) {
                act.finish();
            }
        });
    }
}
