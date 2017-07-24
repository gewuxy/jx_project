package yy.doctor.model.form.text;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
    @IntDef({
            IntentType.location,
            IntentType.hospital,
            IntentType.medicine,
            IntentType.doctor,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntentType {
        int location = 1;
        int hospital = 2;
        int medicine = 3;
        int doctor = 4;
    }


    private int mClick;

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
        setIvIfValid(holder.getIv(), getInt(TForm.drawable));
        setTextIfExist(holder.getTvText(), getString(TForm.name));
    }

    @Override
    public boolean onItemClick(Object host, View v) {
        Intent i = (Intent) getObject(TForm.intent);
        mClick = i.getIntExtra(Extra.KData, 0);
        startActivityForResult(i, getPosition());
        return true;
    }

    @Override
    protected void onActivityResult(int position, Intent data) {
        if (mClick == IntentType.location) {
            String strProvince = data.getStringExtra(Extra.KProvince);
            String strCity = data.getStringExtra(Extra.KCity);
            String strArea = data.getStringExtra(Extra.KDistrict);
            String text;
            if (strArea == null) {
                text = strProvince + " " + strCity;
                put(TForm.name, text);
                save(text, text);
            } else {
                text = strProvince + " " + strCity + " " + strArea;
                put(TForm.name, text);
                save(text, text);
            }
        } else if (mClick == IntentType.hospital) {
            String hospital = data.getStringExtra(Extra.KData);
            put(TForm.name, hospital);
            save(hospital, hospital);
            Integer t = data.getIntExtra(Extra.KId,1);
            put(TForm.data,hospital);
            save(hospital,hospital);
        } else if (mClick == IntentType.medicine) {
            String medicine = data.getStringExtra(Extra.KData);
            put(TForm.name, medicine);
            save(medicine, medicine);
        } else if (mClick == IntentType.doctor) {
            String pro = data.getStringExtra(Extra.KProvince) + data.getStringExtra(Extra.KCity);
            put(TForm.name, pro);
            save(pro, pro);
        }


    }

}
