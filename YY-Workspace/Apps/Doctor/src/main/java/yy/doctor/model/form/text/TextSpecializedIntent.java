package yy.doctor.model.form.text;

import android.content.Intent;
import android.view.View;

import yy.doctor.Extra;
import yy.doctor.R;

/**
 * 专科
 *
 * @auther HuoXuYu
 * @since 2017/7/20
 */

public class TextSpecializedIntent extends TextForm{

    @Override
    public boolean onItemClick(Object host, View v) {
        Intent i = (Intent) getObject(TForm.intent);
        startActivityForResult(i, getPosition());
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_intent;
    }

    @Override
    protected void onActivityResult(int position, Intent data) {
        super.onActivityResult(position, data);

        String str = data.getStringExtra(Extra.KData);
        put(TForm.text, str);
    }
}
