package jx.doctor.sp;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Observable;

import lib.ys.util.sp.SpBase;
import jx.doctor.App;
import jx.doctor.model.config.GlConfigInfo;
import jx.doctor.model.config.GlConfigInfo.TGlConfigInfo;

/**
 * @auther : GuoXuan
 * @since : 2017/8/10
 */

public class SpConfig extends SpBase {

    private static final String KFileName = "sp_config";
    public static final int KDefaultVersion = 0;

    private static SpConfig mInst = null;

    public interface SpConfigKey {
        String KConfigVersion = "gl_config_version";
        String KConfigHospitalLevels = "gl_config_hospital_levels";
    }

    private SpConfig(Context context, String fileName) {
        super(context, fileName);
    }

    @Override
    public void update(Observable o, Object arg) {
        mInst = null;
    }

    public synchronized static SpConfig inst() {
        if (mInst == null) {
            mInst = new SpConfig(App.getContext(), KFileName);
        }
        return mInst;
    }

    public void saveInfo(@NonNull GlConfigInfo config) {
        if (config == null) {
            return;
        } else {
            save(SpConfigKey.KConfigVersion, config.getInt(TGlConfigInfo.version));
            save(SpConfigKey.KConfigHospitalLevels, config.toJson());
        }
    }

    public int getVersion() {
        return getInt(SpConfigKey.KConfigVersion, KDefaultVersion);
    }
}
