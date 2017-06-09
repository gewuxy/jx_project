package yy.doctor.model.form.text;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/5/24
 */

public class FITextRegisterIntent extends FormItem {

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
        return R.layout.form_item_text_register;
    }

    @Override
    protected void refresh(FormItemVH holder) {
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
        if (strArea == null) {
            String text = strProvince + " " + strCity;
            put(TFormElem.name, text);
            save(text, text);
        } else {
            String text = strProvince + " " + strCity + " " + strArea;
            put(TFormElem.name, text);
            save(text, text);
        }
    }

}
