package yy.doctor.model.form.edit;

import android.content.Intent;
import android.view.View;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.Extra;

/**
 * @author CaiXiang
 * @since 2017/5/2
 */
public class EditIntentForm extends EditForm {

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        setOnClickListener(holder.getIv());
    }

    @Override
    protected boolean onViewClick(View v) {
        Intent i = (Intent) getObject(TForm.intent);
        startActivityForResult(i, getPosition());
        return true;
    }

    @Override
    protected void onActivityResult(int position, Intent data) {
        super.onActivityResult(position, data);

        String str = data.getStringExtra(Extra.KData);
        put(TForm.text, str);
    }
}
