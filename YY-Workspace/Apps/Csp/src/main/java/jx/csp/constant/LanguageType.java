package jx.csp.constant;

public enum LanguageType {
    en("en_US"),
    cn_simplified("zh_CN"),
    cn("zh_TW");

    private String mDefine;

    LanguageType(String d) {
        mDefine = d;
    }

    public String define() {
        return mDefine;
    }
}