package yy.doctor.model.form.text;

import android.content.Intent;
import android.view.View;

import lib.bd.location.Place;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * FIXME: 实际上不是专用? 什么意思?
 * pcd专用
 *
 * @author CaiXiang
 * @since 2017/4/28
 */
public class TextIntentForm extends TextForm {

    @Override
    public boolean onItemClick(Object host, View v) {
        Intent i = (Intent) getObject(TForm.intent);
        startActivityForResult(i, getPosition());
        return true;
    }

    @Override
    protected void onActivityResult(int position, Intent data) {
        String p = data.getStringExtra(Extra.KProvince);
        String c = data.getStringExtra(Extra.KCity);
        String d = data.getStringExtra(Extra.KDistrict);

        Place place = new Place(p, c, d);
        String text = place.toString();
        save(text, text);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_intent;
    }
}
