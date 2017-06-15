package lib.ys.util.attr;

import android.util.AttributeSet;

import lib.ys.ConstantsEx;
import lib.ys.YSLog;

/**
 * @author yuansui
 */
public class AttrSetUtil {

    private final static String KNamespaceAndroid = "http://schemas.android.com/apk/res/android";

    public static String getStringValue(AttributeSet set, @AttrFloat @AttrInt String attr) {
        return set.getAttributeValue(KNamespaceAndroid, attr);
    }

    public static float getFloatValue(AttributeSet set, @AttrFloat String attr) {
        String val = getStringValue(set, attr);

        if (attr.equals(AttrFloat.layout_width) || attr.equals(AttrFloat.layout_height)) {
            // like '10.0dip'
            int idx = val.lastIndexOf(AttrParse.suffix_dip);
            if (idx != ConstantsEx.KErrNotFound) {
                // 定义的是dp后缀
                return Float.valueOf(val.substring(0, idx));
            } else {
                // px后缀 或者是 -1 -2
                return Float.valueOf(val);
            }
        }

        return ConstantsEx.KErrNotFound;
    }

    public static int getIntValue(AttributeSet set, @AttrInt String attr) {
        String val = getStringValue(set, attr);

        if (attr.equals(AttrInt.src)) {
            return getSrcValue(set, attr);
        } else if (attr.equals(AttrInt.button)) {
            int ret = getSrcValue(set, attr);
            if (ret == ConstantsEx.KErrNotFound) {
                // hard coding style, string类型文本
            } else {
                return ret;
            }
        }

        return ConstantsEx.KErrNotFound;
    }

    private static int getSrcValue(AttributeSet set, String attr) {
        String val = getStringValue(set, attr);

        // like '@2131492875'
        int index = val.indexOf(AttrParse.prefix_src);
        if (index != ConstantsEx.KErrNotFound) {
            return Integer.valueOf(val.substring(index + 1));
        } else {
            return ConstantsEx.KErrNotFound;
        }
    }

    public static void toString(AttributeSet attrs) {
        int count = attrs.getAttributeCount();
        YSLog.d("www", "toString: count  = " + count);

        for (int i = 0; i < count; ++i) {
            YSLog.d("www", "toString: name = " + attrs.getAttributeName(i));
//            LogMgr.d("www", "toString: name res = " + set.getAttributeNameResource(i));
            YSLog.d("www", "toString: name value = " + attrs.getAttributeValue(i));


        }
    }
}
