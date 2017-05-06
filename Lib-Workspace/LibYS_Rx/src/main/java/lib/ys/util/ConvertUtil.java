package lib.ys.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lib.ys.ConstantsEx;

public class ConvertUtil {

    private static final String TAG = ConvertUtil.class.getSimpleName();

    private static final int BYTES_LEN = 1024;
    private static final String HEX_CODE = "0123456789ABCDEF";

    /**
     * Inputstream转String
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStreamToStr(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is.available() == 0) {
            byte[] bytes = new byte[BYTES_LEN];
            int len = ConstantsEx.KErrNotFound;
            while ((len = is.read(bytes)) != ConstantsEx.KErrNotFound) {
                baos.write(bytes, 0, len);
            }
        } else {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            baos.write(bytes);
        }

        return new String(baos.toByteArray()).replaceAll("\n", "");
    }


    /**
     * String转InputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream strToInputStream(String in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }

    /**
     * 16进制转10进制
     *
     * @param hexStr
     * @return
     */
    public static String hexStrToStr(String hexStr) {
        if (hexStr == null || hexStr.equals("")) {
            return null;
        }

        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = HEX_CODE.indexOf(hexs[2 * i]) * 16;
            n += HEX_CODE.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 10进制转16进制
     *
     * @param str
     * @return
     */
    public static String strToHexStr(String str) {
        char[] chars = HEX_CODE.toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * bytes转换成16进制的string
     *
     * @param bytes
     * @return
     */
    public static String bytesTohexStr(byte[] bytes) {
        String hex = "";
        for (int i = 0; i < bytes.length; i++) {
            hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
        }

        return hex;
    }
}
