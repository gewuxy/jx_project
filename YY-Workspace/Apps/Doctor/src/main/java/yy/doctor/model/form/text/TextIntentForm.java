package yy.doctor.model.form.text;

import android.content.Intent;
import android.view.View;

import yy.doctor.Extra;
import yy.doctor.util.Util;

/**
 * pcd专用
 *
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
        String p = data.getStringExtra(Extra.KProvince);
        String c = data.getStringExtra(Extra.KCity);
        String d = data.getStringExtra(Extra.KDistrict);

        String text = Util.generatePcd(p, c, d);
        save(text, text);
    }

}
