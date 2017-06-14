package lib.ys.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import lib.ys.AppEx;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;

/**
 * 生成设备唯一ID的工具
 *
 * @author yuansui
 */
public class DeviceIdUtil {
    private static final String TAG = DeviceIdUtil.class.getSimpleName();

    private static final String KPrefFile = "device_id.xml";
    private static final String KPrefDeviceId = "device_id";

    private static String mId;

    public static String getId() {
        if (mId != null) {
            return mId;
        }

        Context context = AppEx.getContext();
        String id = null;

        SharedPreferences pref = context.getSharedPreferences(KPrefFile, 0);
        id = pref.getString(KPrefDeviceId, null);
        if (id != null) {
            mId = id;
            return mId;
        }

        try {
            id = DeviceUtil.getAndroidId(context);
            if (TextUtil.isNotEmpty(id) && !id.equals("9774d56d682e549c")) {
                return putId(pref, UUID.nameUUIDFromBytes(id.getBytes(ConstantsEx.KEncoding_utf8)));
            }

            id = DeviceUtil.getIMEI(context);
            if (TextUtil.isNotEmpty(id)) {
                return putId(pref, UUID.nameUUIDFromBytes(id.getBytes(ConstantsEx.KEncoding_utf8)));
            } else {
                return putId(pref, UUID.randomUUID());
            }
        } catch (Exception e) {
            YSLog.e(TAG, "getId", e);
        }

        return mId;
    }

    private static String putId(SharedPreferences pref, UUID uuid) {
        mId = uuid.toString();
        pref.edit().putString(KPrefDeviceId, mId).commit();
        return mId;
    }
}
