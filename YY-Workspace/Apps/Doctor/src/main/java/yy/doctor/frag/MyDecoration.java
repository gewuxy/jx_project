package yy.doctor.frag;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

import lib.ys.view.recycler.DividerGridItemDecoration;

/**
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class MyDecoration extends DividerGridItemDecoration {
    public MyDecoration(Context context) {
        super(context);
    }

    @Override
    public void drawHorizontal(Canvas c, RecyclerView parent) {
    }
}
