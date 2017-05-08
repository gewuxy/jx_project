package yy.doctor.activity;

import org.json.JSONException;

/**
 * @auther yuansui
 * @since 2017/5/6
 */

public interface EValParser {
    <T> T parse(String text, Class<T> c) throws JSONException;
}
