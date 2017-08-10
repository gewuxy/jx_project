package yy.doctor.sp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import lib.ys.util.sp.SpBase;
import yy.doctor.App;
import yy.doctor.model.GlConfigInfo;
import yy.doctor.model.GlConfigInfo.TGlConfigInfo;

/**
 * @auther : GuoXuan
 * @since : 2017/8/10
 */

public class SpConfig extends SpBase {

    private static final String KFileName = "sp_config";

    private static SpConfig mInst = null;

    private interface SpConfigKey {
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

    public void saveInfo(GlConfigInfo config) {
        if (config == null) {
            return;
        } else {
            save(SpConfigKey.KConfigVersion, config.getInt(TGlConfigInfo.version));
            save(SpConfigKey.KConfigHospitalLevels, (ArrayList)config.getList(TGlConfigInfo.propList));
        }
    }

    public int getVersion() {
        if (mInst == null) {
            inst();
        }
        return mInst.getInt(SpConfigKey.KConfigVersion);
    }

    public List getHospitalLevels() {
        if (mInst == null) {
            inst();
        }
        return (ArrayList) mInst.getSerializable(SpConfigKey.KConfigHospitalLevels);
    }
}
