package jx.csp.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.dialog.CommonDialog1;
import jx.csp.dialog.CommonDialog2;
import jx.csp.dialog.PreviewDialog;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * @auther yuansui
 * @since 2017/6/8
 */

public class UISetter {

    /**
     * 设置密码的输入范围格式
     *
     * @param et EditText
     */
    public static void setPwdRange(EditText et) {

        et.setKeyListener(new NumberKeyListener() {

            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                String chars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM-×÷＝%√°′″{}()[].|*/#~,:;?\"‖&*@\\^,$–…'=+!><.-—_";
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });
    }

    /**
     * NavBar中间文字设置 文字太长时的设置方法
     */
    public static void setNavBarMidText(NavBar bar, String fileName, Context context) {
        if (TextUtil.isEmpty(fileName)) {
            return;
        }
        View v = View.inflate(context, R.layout.layout_nav_bar_mid_text, null);
        TextView tv = v.findViewById(R.id.nav_bar_mid_tv);
        tv.setText(fileName);
        bar.addViewMid(v);
    }

    /**
     * 弹出账号冻结对话框
     *
     * @param s 冻结内容
     * @param c 上下文
     */
    public static void showFrozenDialog(String s, Context c) {
        CommonDialog1 d = new CommonDialog1(c);
        d.setTitle(R.string.account_frozen);
        d.setContent(s);
        d.addButton(R.string.confirm, R.color.black, null);
        d.show();
    }

    /**
     * 删除会议
     *
     * @param courseId courseId
     * @param context  context
     */
    public static void showDeleteMeet(String courseId, Context context) {
        CommonDialog2 d = new CommonDialog2(context);
        d.setHint(R.string.ensure_delete);
        d.addBlackButton(R.string.cancel);
        d.addBlackButton(R.string.confirm, v1 ->
                CommonServRouter.create(ReqType.share_delete_meet)
                        .courseId(courseId)
                        .route(context)
        );
        d.show();
    }

    /**
     * 相册没有权限
     *
     * @param context context
     */
    public static void photoNoPermission(Context context) {
        CommonDialog1 d = new CommonDialog1(context);
        d.setContent(R.string.to_set_photo);
        d.addButton(R.string.cancel, R.color.text_404356, null);
        d.addButton(R.string.to_set, R.color.text_333, l -> Util.toSetting());
        d.show();
    }

    /**
     * 拍照没有权限
     *
     * @param context context
     */
    public static void cameraNoPermission(Context context) {
        CommonDialog1 d = new CommonDialog1(context);
        d.setContent(R.string.to_set_camera);
        d.addButton(R.string.cancel, R.color.text_404356, null);
        d.addButton(R.string.open, R.color.text_333, l -> Util.toSetting());
        d.show();
    }

    /**
     * 预览主题
     *
     * @param c     Context
     * @param theme 主题
     * @param photo 图片
     */
    public static void previewTheme(Context c, String theme, String photo) {
        PreviewDialog dialog = new PreviewDialog(c, theme, photo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
