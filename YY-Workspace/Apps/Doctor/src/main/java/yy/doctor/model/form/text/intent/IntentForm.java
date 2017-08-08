package yy.doctor.model.form.text.intent;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.bd.location.Place;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.form.text.TextForm;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class IntentForm extends TextForm {

    @IntDef({
            IntentType.un_know,
            IntentType.location,
            IntentType.hospital,
            IntentType.medicine,
            IntentType.section,
            IntentType.cme_number,
            IntentType.certification,
            IntentType.skill,
            IntentType.set_phone,
            IntentType.set_email,
            IntentType.set_pwd,
            IntentType.doctor_title,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntentType {
        int un_know = 0;
        int location = 1;
        int hospital = 2;
        int medicine = 3;
        int section = 5;
        int cme_number = 6;
        int certification = 7;
        int skill = 8;
        int set_phone = 10;
        int set_email = 11;
        int set_pwd = 12;
        int doctor_title = 13;
    }

    private int mCurrType = IntentType.un_know;

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        mCurrType = getType();
    }

    @Override
    public boolean onItemClick(Object host, View v) {
        if (mCurrType == IntentType.un_know) {
            // 不处理
            return false;
        }

        Intent i = getIntent();
        if (i != null) {
            startActivityForResult(i, getPosition());
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int position, Intent data) {
        switch (mCurrType) {
            case IntentType.location: {
                String p = data.getStringExtra(Extra.KProvince);
                String c = data.getStringExtra(Extra.KCity);
                String d = data.getStringExtra(Extra.KDistrict);

                Place place = new Place(p, c, d);
                String text = place.toString();
                save(text, text);
            }
            break;
            case IntentType.hospital: {
                String hospital = data.getStringExtra(Extra.KData);
                save(hospital, hospital);
                int id = data.getIntExtra(Extra.KId, R.mipmap.hospital_level_three);
                drawable(id);
            }
            break;
            case IntentType.medicine: {
                String medicine = data.getStringExtra(Extra.KName) + " " + data.getStringExtra(Extra.KData);
                save(medicine, medicine);
            }
            break;
            case IntentType.section: {
                String section = data.getStringExtra(Extra.KData);
                save(section, section);
            }
            break;
            case IntentType.cme_number: {
                String cme_number = data.getStringExtra(Extra.KData);
                save(cme_number, cme_number);
            }
            break;
            case IntentType.certification: {
                String certification = data.getStringExtra(Extra.KData);
                save(certification, certification);
            }
            break;
            case IntentType.skill: {
                String skill = data.getStringExtra(Extra.KData);
                save(skill, skill);
            }
            break;
            case IntentType.set_phone: {
                String mobile = data.getStringExtra(Extra.KData);
                save(mobile, mobile);
            }
            break;
            case IntentType.set_email: {
                String email = data.getStringExtra(Extra.KData);
                save(email, email);
            }
            break;
            case IntentType.doctor_title: {
                String title = data.getStringExtra(Extra.KData);
                save(title, title);
            }
            break;
        }
    }
}