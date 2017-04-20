package lib.ys.util;

import android.text.InputFilter;
import android.widget.EditText;

import java.text.DecimalFormat;

import lib.ys.ConstantsEx;

public class NumberUtil {

    private static final String KDecimalHead = "0.";
    private static final String KDecimalSymbol = "0";

    /**
     * 限制小数点后的位数, 强制保留
     *
     * @param value
     * @param keepCount 强制保留的位数
     * @return
     */
    public static String limitFloatDecimal(float value, int keepCount) {
        StringBuffer sBuffer = new StringBuffer(KDecimalHead);
        for (int i = 0; i < keepCount; ++i) {
            sBuffer.append(KDecimalSymbol);
        }
        DecimalFormat df = new DecimalFormat(sBuffer.toString());
        String format = df.format(value);
        return format;
    }

    public static String limitDoubleDecimal(double value, int keepCount) {
        StringBuffer sBuffer = new StringBuffer(KDecimalHead);
        for (int i = 0; i < keepCount; ++i) {
            sBuffer.append(KDecimalSymbol);
        }
        DecimalFormat df = new DecimalFormat(sBuffer.toString());
        String format = df.format(value);
        return format;
    }

    public static String limitValue(double value, int keepCount) {
        String string = limitDoubleDecimal(value, keepCount);
        StringBuffer buffer = new StringBuffer(string);
        while (buffer.charAt(buffer.length() - 1) == '0') {
            buffer = buffer.deleteCharAt(buffer.length() - 1);
        }
        if (buffer.charAt(buffer.length() - 1) == '.') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }

    public static void limitEtInputLength(EditText et, int length) {
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    /**
     * 确保alpha的在有效范围内
     *
     * @param alpha
     * @return
     */
    public static int limitAlpha(float alpha) {
        if (alpha < ConstantsEx.KAlphaMin) {
            alpha = ConstantsEx.KAlphaMin;
        } else if (alpha > ConstantsEx.KAlphaMax) {
            alpha = ConstantsEx.KAlphaMax;
        }
        return (int) alpha;
    }

    /**
     * 把数字转换成int型(丢精度)
     *
     * @param value
     * @return
     */
    public static String toIntStyle(String value) {
        float moneyF = Float.valueOf(value);
        int money = (int) moneyF;
        return String.valueOf(money);
    }
}
