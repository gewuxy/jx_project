package yy.doctor.model.form.text;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/5/24
 */

public class TextRegisterIntentForm extends BaseForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.text_register_intent;
    }

    @Override
    public boolean check() {
        return checkInput();
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_register;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);
        setTextIfExist(holder.getTvText(), getString(TFormElem.name));
    }

    @Override
    public boolean onItemClick(Object host, View v) {
        Intent i = (Intent) getObject(TFormElem.intent);
        startActivityForResult(i, getPosition());
        return true;
    }

    @Override
    protected void onActivityResult(int position, Intent data) {

        String strProvince = data.getStringExtra(Extra.KProvince);
        String strCity = data.getStringExtra(Extra.KCity);
        String strArea = data.getStringExtra(Extra.KArea);
        String text;
        if (strArea == null) {
            text = strProvince + " " + strCity;
            put(TFormElem.name, text);
            save(text, text);
        } else {
            text = strProvince + " " + strCity + " " + strArea;
            put(TFormElem.name, text);
            save(text, text);
        }

    }

}
