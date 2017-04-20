package lib.ys.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ConnectivityReceiver extends BaseReceiver {

    /**
     * 网络类型码，如3G、4G等
     */
    public enum TConnType {
        disconnect, // 无网络
        type_2g, //
        type_3g, //
        type_4g, //
        type_wifi, //
        type_unknow, //
    }

    private TConnType mType = TConnType.type_unknow;
    private ConnectivityManager mCm;

    public ConnectivityReceiver(Context context) {
        super(context);
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(this, filter);

        mCm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        setType();
    }

    private void setType() {
        NetworkInfo info = mCm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI: {
                    mType = TConnType.type_wifi;
                }
                break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: {
                            mType = TConnType.type_2g;
                        }
                        break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP: {
                            mType = TConnType.type_3g;
                        }
                        break;
                        case TelephonyManager.NETWORK_TYPE_LTE: {
                            mType = TConnType.type_4g;
                        }
                        break;
                        default: {
                            mType = TConnType.type_unknow;
                        }
                    }
                    break;
                default:
                    mType = TConnType.type_unknow;
            }
        } else {
            mType = TConnType.disconnect;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setType();
    }

    public TConnType getType() {
        return mType;
    }
}
