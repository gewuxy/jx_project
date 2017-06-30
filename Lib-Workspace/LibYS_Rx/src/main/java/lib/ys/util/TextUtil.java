package lib.ys.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import lib.ys.ConstantsEx;

public class TextUtil {

    public static final String KTextEmpty = ConstantsEx.KEmptyValue;
    public static final String KBlankUtf8Def = "%20";
    public static final int KBlankIndex = 32;

    /**
     * 将String中的字符全角化。即将所有的数字、字母及标点全部转为全角字符，使它们与汉字同占两个字节
     *
     * @param input
     * @return
     */
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String cnSymbolToEnSymbol(String str) throws PatternSyntaxException {
        str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    private static final int KCNRangeMin = 19968; // \u4e00
    private static final int KCNRangeMax = 171941; // \u9fa5

    /**
     * 根据字体大小及限制的PX长度来截取字符串
     *
     * @param text     原字符串
     * @param textSize 字体像素大小
     * @param pxLength 像素长度限制
     * @param symbol   超时长度限制时追加的标识
     * @return 截取后的字符串
     */
    public static String cutString(CharSequence text, float textSize, float pxLength, String symbol) {
        float oneCharLen = textSize;
        float halfCharlen = textSize / 2;

        int curLen = 0;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            int chr1 = text.charAt(i);
            if (chr1 >= KCNRangeMin && chr1 <= KCNRangeMax) {
                // CN
                curLen += oneCharLen;
            } else {
                curLen += halfCharlen;
            }

            if (curLen > pxLength) {
                break;
            }

            count++;
        }

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(text.subSequence(0, count));

        if (curLen > pxLength && !isEmpty(symbol)) {
            sBuilder.append(symbol);
        }

        return sBuilder.toString();
    }

    public static String toString(Object object) {
        if (object instanceof Double) {
            return Double.toString((Double) object);
        } else if (object instanceof Integer) {
            return Integer.toString((Integer) object);
        }
        return object.toString();
    }

    /**
     * 过滤空串
     *
     * @param str
     * @return
     */
    public static String filterNull(String str) {
        return str == null ? KTextEmpty : str;
    }


    /**
     * 把url的中文转换url格式
     *
     * @param s
     * @return
     */
    public static String toUtf8(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c >= 0 && c <= 255) {
                // 非中文
                if (c == KBlankIndex) {
                    /**
                     * 空格需要特殊处理
                     * PS: 不能使用{@link java.net.URLEncoder#encode(String)}处理，会变成'+'，浏览器无法辨识
                     */
                    sb.append(KBlankUtf8Def);
                } else {
                    sb.append(c);
                }
            } else {

                byte[] b;
                try {
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    b = new byte[0];
                }

                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0) {
                        k += 256;
                    }
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
}
