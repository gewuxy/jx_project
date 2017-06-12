package lib.network;

import android.support.annotation.NonNull;

import java.util.List;

import lib.network.model.param.BasePair;
import lib.network.model.param.CommonPair;

/**
 * http里使用的一些小工具
 *
 * @author yuansui
 */
public class NetworkUtil {

    public static final String KTextEmpty = "";

    private static final char KSymbolQuestion = '?';
    private static final char KSymbolAnd = '&';
    private static final char KSymbolEqual = '=';


    /**
     * @param url
     * @param params
     * @return
     */
    public static String generateGetUrl(@NonNull String url, @NonNull List<CommonPair> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(url)
                .append(KSymbolQuestion)
                .append(generateGetParams(params));

        return sb.toString();
    }

    /**
     * 按照格式生成get参数
     *
     * @param params
     * @return
     */
    public static String generateGetParams(List<CommonPair> params) {
        if (params == null || params.isEmpty()) {
            return KTextEmpty;
        }

        StringBuilder sb = new StringBuilder();
        BasePair pair;
        int size = params.size();
        for (int i = 0; i < size; i++) {
            pair = params.get(i);
            if (pair == null) {
                continue;
            }
            sb.append(pair.getName());
            sb.append(KSymbolEqual);
            sb.append(pair.getVal());

            if (i != size - 1) {
                sb.append(KSymbolAnd);
            }
        }

        return sb.toString();
    }
}
