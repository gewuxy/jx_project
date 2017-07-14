package lib.ys.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则验证
 */
public class RegexUtil {

    public interface RegexRule {
        String KMail = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        String KInteger = "^[0-9]*$";
        String KDecimal = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
        /**
         * 中国区, 手机号码
         */
        String KMobileCn = "^(1(2|3|4|5|6|7|8|9)[0-9])\\d{8}$";
        String KChinese = "^[\\u4E00-\\u9FA5]+$";
    }

    /**
     * 验证邮箱
     *
     * @param str 待验证的字符串
     * @return 如果是符合的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isEmail(String str) {
        return match(RegexRule.KMail, str);
    }

    /**
     * 验证数字输入
     *
     * @param str 待验证的字符串
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isInteger(String str) {
        return match(RegexRule.KInteger, str);
    }

    public static boolean isDecimal(String text) {
        return match(RegexRule.KDecimal, text);
    }

    /**
     * 检验是否是电话号码
     */
    public static boolean isMobileCN(CharSequence phone) {
        return match(RegexRule.KMobileCn, phone);
    }

    /**
     * 检查输入的数据中是否有特殊字符
     *
     * @param input 要检查的数据
     * @param regex 特殊字符正则表达式
     * @return boolean 如果包含正则表达式 <code> regx </code> 中定义的特殊字符，返回true； 否则返回false
     */
    private static boolean hasCrossScriptRisk(CharSequence input, String regex) {
        if (input != null) {
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(input);
            return m.find();
        }
        return false;
    }

    /**
     * 检查输入的数据中是否有特殊字符
     *
     * @param input 要检查的数据
     * @return boolean 如果包含正则表达式 <code> regex </code> 中定义的特殊字符，返回true； 否则返回false
     */
    public static boolean checkSpecialSymbol(CharSequence input) {
        // String regx =
        // "!|！|@|◎|#|＃|(\\$)|￥|%|％|(\\^)|……|(\\&)|※|(\\*)|×|(\\()|（|(\\))|）|_|——|(\\+)|＋|(\\|)|§ ";
        String regex = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        return hasCrossScriptRisk(input, regex);
    }

    /**
     * 是否空格
     *
     * @param blankSpace
     * @return
     */
    public static boolean isBlankSpace(String blankSpace) {
        String regex = "\\s+";
        return Pattern.matches(regex, blankSpace);
    }

    /**
     * 是否中文
     *
     * @param text
     * @return
     */
    public static boolean isChinese(String text) {
        return match(RegexRule.KChinese, text);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回<b>true</b>, 否则返回 <b>false</b>;
     */
    public static boolean match(String regex, CharSequence str) {
        if (!TextUtils.isEmpty(str)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
        return false;
    }
}
