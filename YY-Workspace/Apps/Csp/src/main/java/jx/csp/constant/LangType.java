package jx.csp.constant;

public enum LangType {
    en("en_US"),
    cn_simplified("zh_CN"),
    cn("zh_TW");

    private String mDefine;

    LangType(String d) {
        mDefine = d;
    }

    public String define() {
        return mDefine;
    }
}