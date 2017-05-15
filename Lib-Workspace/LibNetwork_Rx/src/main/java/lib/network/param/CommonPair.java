package lib.network.param;

/**
 * 普通键值对
 */
public class CommonPair extends BasePair<String> {

    public CommonPair(String name, Object obj) {
        super(name, String.valueOf(obj));
    }
}
