package lib.ys.util;

/**
 * 测量size的工具
 *
 * @author yuansui
 */
public class SizeofUtil {
    private static long[] KLongSizeTable;

    /**
     * 判断long有多少位
     *
     * @param x
     * @return
     */
    public static int ofLong(long x) {
        if (KLongSizeTable == null) {
            KLongSizeTable = new long[]{9l, 99l, 999l, 9999l, 99999l, 999999l, 9999999l,
                    99999999l, 999999999l, 9999999999l, 99999999999l,
                    999999999999l, 9999999999999l, 99999999999999l, 999999999999999l,
                    9999999999999999l, 99999999999999999l, 999999999999999999l,
                    Long.MAX_VALUE};
        }
        for (int i = 0; ; i++)
            if (x <= KLongSizeTable[i]) {
                return i + 1;
            }
    }
}
