package yy.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.OnClickListener;

import lib.sliding.menu.SlidingMenu;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseVPActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.HomeFrag;

public class MainActivity extends BaseVPActivity {

    public static void nav(Context context, String test) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.KTestAble, test);
        LaunchUtil.startActivity(context, intent);
    }

    @Override
    public void initData() {
        getIntent().getStringExtra(Extra.KTestAble);

        add(new HomeFrag());
    }

    @Override
    public void initTitleBar() {
        getTitleBar().addTextViewMid("test");
        getTitleBar().addImageViewLeft(R.mipmap.ic_loading, new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        setMenu();
    }

    private void setMenu() {
        SlidingMenu m = new SlidingMenu(this);
        m.setMode(SlidingMenu.LEFT);
        m.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        m.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        m.setShadowWidthRes(R.dimen.shadow_width);
        m.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        m.setShadowDrawable(R.drawable.menu_shadow);
        m.setFadeDegree(0.25f);
        m.setBackgroundImage(R.mipmap.menu_bg);
        m.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        m.setMenu(R.layout.layout_menu);

        // 收缩动画 侧滑栏
        m.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen * 0.25 + 0.75);
                // 后两个数，是变化的中心点参数
                canvas.scale(scale, scale, -canvas.getWidth() / 2,
                        canvas.getHeight() / 2);
            }
        });

        // 收缩动画 主界面
        m.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv: {

            }
            break;
        }
    }

}
