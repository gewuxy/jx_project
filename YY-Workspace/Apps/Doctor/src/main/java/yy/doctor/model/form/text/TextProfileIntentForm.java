package yy.doctor.model.form.text;

import android.content.Intent;
import android.view.View;

import yy.doctor.R;

/**
 * 我的资料
 *
 * @author HuoXuYu
 * @since 2017/7/20
 */
public class TextProfileIntentForm extends TextForm {

    @Override
    public boolean onItemClick(Object host, View v) {
        Intent i = (Intent) getObject(TForm.intent);
        startActivity(i);
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_intent;
    }
}
