package yy.doctor.model.hospital;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther WangLan
 * @since 2017/7/19
 */

public interface IHospital {
    @IntDef({
            HospitalType.hospital_title,
            HospitalType.hospital_data,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface HospitalType {
        int hospital_title = 0;
        int hospital_data = 1;
    }

    @HospitalType
    int getType();
}
