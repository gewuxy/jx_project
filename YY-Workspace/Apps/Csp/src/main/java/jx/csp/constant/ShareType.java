package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
@IntDef({
        ShareType.wechat,
        ShareType.wechat_friend,
        ShareType.qq,
        ShareType.linkedin,
        ShareType.sina,
        ShareType.facebook,
        ShareType.twitter,
        ShareType.whatsapp,
        ShareType.line,
        ShareType.sms,
        ShareType.dingding,
        ShareType.contribute,
        ShareType.preview,
        ShareType.watch_pwd,
        ShareType.copy_link,
        ShareType.delete,
        ShareType.editor,
})
@Retention(RetentionPolicy.SOURCE)
public @interface ShareType {
    int wechat = 0;     //微信
    int wechat_friend = 1;//微信朋友圈
    int qq = 2;         //QQ
    int linkedin = 3;   //领英
    int sina = 4;       //微博
    int facebook = 5;   //facebook
    int twitter = 6;    //twitter
    int whatsapp = 7;   //whatsapp
    int line = 8;       //Line
    int sms = 9;        //短信
    int dingding = 10;  //钉钉
    int contribute = 11;//投稿

    int preview = 12;   //预览
    int watch_pwd = 13; //观看密码
    int copy_link = 14; //复制链接
    int delete = 15;    //删除会议

    int editor = 16;    //编辑
}