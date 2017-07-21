package yy.doctor.model.form.text;

import android.content.Intent;
import android.view.View;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;

/**
 *
 *
 * @author HuoXuYu
 * @since 2017/7/14
 */
public class TextAcademicIntentForm extends TextForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_academic;
    }


    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        setTextIfExist(holder.getTvText(), getString(TForm.text));
        setTextIfExist(holder.getTvName(), getString(TForm.name));
        save(getString(TForm.text), getString(TForm.text));

    }

    @Override
    public boolean onItemClick(Object host, View v) {
        Intent i = (Intent) getObject(TForm.intent);
        startActivityForResult(i, getPosition());
        return true;
    }
}
