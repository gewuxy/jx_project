package yy.doctor.model.form.text;

import android.content.Intent;
import android.view.View;

import yy.doctor.Extra;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class TextIntentForm extends TextForm {

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
        //判断是否有区县
        if (strArea == null) {
            String text = strProvince + " " + strCity;
            save(text, text);
        } else {
            String text = strProvince + " " + strCity + " " + strArea;
            save(text, text);
        }
    }

}
