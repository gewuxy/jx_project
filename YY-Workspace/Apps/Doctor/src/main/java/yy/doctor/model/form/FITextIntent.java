package yy.doctor.model.form;

import android.content.Intent;
import android.view.View;

import lib.ys.LogMgr;
import yy.doctor.Extra;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class FITextIntent extends FIText {

    @Override
    public boolean onItemClick(Object host, View v) {
        Intent i = (Intent) getObject(TFormElem.intent);
        startActivityForResult(i, getPosition());
        return true;
    }

    @Override
    protected void onActivityResult(int position, Intent data) {
        String str = data.getStringExtra(Extra.KData);
        LogMgr.e(TAG,str);
        put(TFormElem.text, str);
    }
}
