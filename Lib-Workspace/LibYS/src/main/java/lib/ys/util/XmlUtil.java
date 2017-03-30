package lib.ys.util;

import android.graphics.Color;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import lib.ys.ConstantsEx;

public class XmlUtil {

    private static final String KFloatSign = ".";
    public static final String KValueInvalid = "invalid";

    public static boolean isValueValid(String value) {
        if (value != null & value.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static float getNumberValue(String src) throws NumberFormatException {
        float ret;
        if (src.indexOf(KFloatSign) != ConstantsEx.KErrNotFound) {
            // it is a float value
            ret = Float.parseFloat(src);
        } else {
            // it is a int value
            ret = Integer.parseInt(src);
        }

        return ret;
    }

    public static String getStringAttr(XmlPullParser parser, String name) {
        String value = parser.getAttributeValue(null, name);
        if (!TextUtils.isEmpty(value)) {
            return value;
        }
        return KValueInvalid;
    }

    public static String getStringAttr(XmlPullParser parser, Enum e) {
        return XmlUtil.getStringAttr(parser, e.name());
    }

    public static boolean getBooleanAttr(XmlPullParser parser, String name) {
        String value = parser.getAttributeValue(null, name);
        if (!TextUtils.isEmpty(value)) {
            return Boolean.parseBoolean(value.toLowerCase());
        }
        return false;
    }

    public static int getColorAttr(XmlPullParser parser, String name) {
        String color = parser.getAttributeValue(null, name);
        if (!TextUtils.isEmpty(color)) {
            int tmpColor = Integer.parseInt(color, 16);
            return Color.rgb(Color.red(tmpColor), Color.green(tmpColor), Color.blue(tmpColor));
        }
        return 0;
    }

    public static int getIntAttr(XmlPullParser parser, String name) {
        String value = parser.getAttributeValue(null, name);
        if (!TextUtils.isEmpty(value)) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    public static float getFloatAttr(XmlPullParser parser, String name) {
        String value = parser.getAttributeValue(null, name);
        if (!TextUtils.isEmpty(value)) {
            return Float.parseFloat(value);
        }
        return 0;
    }
}
