package lib.ys.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import lib.ys.ConstantsEx;
import lib.ys.YSLog;

public class MoneyUtil {

    private static final String TAG = MoneyUtil.class.getSimpleName();

    /**
     * 把金钱格式转换成double, 数额不能超出double范围
     *
     * @param money
     * @return 返回double格式的价格, 异常情况返回 0
     */
    public static double getDouble(String money) {
        double ret = 0;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        try {
            Number number = numberFormat.parse(money);
            BigDecimal decimal = new BigDecimal(number.toString());
            ret = decimal.doubleValue();
//			LogMgr.d(TAG, "number = " + number.toString());
//			LogMgr.d(TAG, "ret = " + ret);
        } catch (ParseException e) {
            YSLog.e(TAG, e);
            try {
                ret = Double.valueOf(money);
            } catch (NumberFormatException e2) {
                YSLog.e(TAG, e2);
            }
        }

        return ret;
    }

    private static final String KDecimalFormat = "0.00";
    private static final String KZero = "0";

    /**
     * 获取正常数字格式的金钱字符串, 保证在字符串有小数的时候, 小数点后一定会显示两位数字包括0
     *
     * @param money
     * @return 正常情况的字符串类似"4590.10"或"4300", 异常情况返回"0"
     */
    public static String getNumberString(String money) {
        String ret = KZero;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        try {
            Number number = numberFormat.parse(money);
            BigDecimal decimal = new BigDecimal(number.toString());
            ret = decimal.toString();

            DecimalFormat df = new DecimalFormat(KDecimalFormat);
            ret = df.format(ret);
        } catch (ParseException e) {
            YSLog.e(TAG, e);
        }

        return ret;
    }

    private static final String KFloatDot = ".";
    private static final int KThousand = 3;
    private static final String KMoneyDot = ",";

    /**
     * 把数字格式转换成金钱格式
     *
     * @param text
     * @return
     */
    public static String getMoney(String text) {
        boolean minus = false;
        String minusSymbol = "-";
        if (minus = (text.startsWith(minusSymbol))) {
            text = text.substring(1);
        }
        int indexDot = text.indexOf(KFloatDot);
        StringBuffer sInteger = new StringBuffer();
        String decimals = null;
        if (indexDot != ConstantsEx.KErrNotFound) {
            sInteger.append(text.substring(0, indexDot));
            decimals = text.substring(indexDot);
        } else {
            sInteger.append(text);
            decimals = ConstantsEx.KEmptyValue;
        }
        int i = sInteger.length();
        i -= KThousand;
        while (i > 0) {
            sInteger.insert(i, KMoneyDot);
            i -= KThousand;
        }
        return minus ? (minusSymbol + sInteger.toString() + decimals) : (sInteger.toString() + decimals);

//		int floatDotIndex = floatDecimalStr.indexOf(KFloatDot);
//		String strBeforeFloatDot = null;
//		String strAfterFloatDot = null;
//		if (floatDotIndex != -1) {
//			strBeforeFloatDot = floatDecimalStr.substring(0, floatDotIndex);
//			strAfterFloatDot = floatDecimalStr.substring(floatDotIndex);
//		}else {
//			strBeforeFloatDot = floatDecimalStr;
//			strAfterFloatDot = "";
//		}
//		ArrayList<Integer> arrayList = StringUtil.getGroupIndexs(strBeforeFloatDot, KThousand);
//		int j = 0;
//		for (int i = 0; i < arrayList.size(); i++) {
//			int strBeforeFloatDotLen = strBeforeFloatDot.length();
//			strBeforeFloatDot = StringUtil.insert(strBeforeFloatDot, strBeforeFloatDotLen - (arrayList.get(i) + j), ",");
//			j++;
//		}
//		return strBeforeFloatDot + strAfterFloatDot;
    }
}
