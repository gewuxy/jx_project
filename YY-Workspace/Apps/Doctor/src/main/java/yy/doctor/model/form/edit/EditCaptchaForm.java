package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import lib.ys.util.res.ResLoader;
import lib.yy.adapter.VH.FormVH;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @auther WangLan
 * @since 2017/7/12
 */

public class EditCaptchaForm extends EditForm implements OnCountDownListener {
    FormVH h;
    private CountDown mCountDown;

    @NonNull
    @Override
    public int getType() {
        return FormType.et_captcha;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        h = holder;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit_captcha;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        boolean enable = getBoolean(TForm.enable);
        TextView tv = holder.getTvText();
        if (enable) {
            setOnClickListener(tv);
        } else {
            removeOnClickListener(tv);
        }

        int textColor = getInt(TForm.text_color);
        if (textColor != 0) {
            tv.setSelected(enable);
            tv.setTextColor(ResLoader.getColorStateList(textColor));
        }
    }

    @Override
    protected boolean onViewClick(View v) {

        return false;
    }
    public void change(){
        if (mCountDown == null) {
            mCountDown = new CountDown();
            mCountDown.setListener(this);
        }
        mCountDown.start(TimeUnit.SECONDS.toSeconds(6));
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            // 倒计时结束
            h.getTvText().setText("重新获取");
            h.getTvText().setClickable(true);

        } else {
            h.getTvText().setText(remainCount + "秒");
            h.getTvText().setClickable(false);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    public void recycle() {
        if (mCountDown != null) {
            mCountDown.recycle();
        }

    }
}
