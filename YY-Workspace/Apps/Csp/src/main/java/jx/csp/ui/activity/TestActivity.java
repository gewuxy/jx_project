package jx.csp.ui.activity;

import android.content.Intent;

import jx.csp.ui.activity.livevideo.LiveVideoActivity;
import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.login.CaptchaLoginActivity;
import jx.csp.ui.activity.login.CaptchaLoginNicknameActivity;
import jx.csp.ui.activity.login.EmailLoginActivity;
import jx.csp.ui.activity.login.SkipActivity;
import jx.csp.ui.activity.me.ContributePlatformActivity;
import jx.csp.ui.activity.me.bind.BindEmailActivity;
import jx.csp.ui.activity.me.bind.BindPhoneActivity;
import jx.csp.ui.activity.me.bind.ChangePwdActivity;
import lib.jx.test.BaseTestActivity;
import lib.ys.YSLog;

/**
 * @auther yuansui
 * @since 2017/9/20
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {

        add("首页", new Intent(this, MainActivity.class));
        add("邮箱登录", new Intent(this, EmailLoginActivity.class));
        add("登录", new Intent(this, AuthLoginActivity.class));
        add("手机登录", new Intent(this, CaptchaLoginActivity.class));
        add("guide", GuideActivity.class);
        add("live video", LiveVideoActivity.class);
        add("手机昵称", CaptchaLoginNicknameActivity.class);
        add("注册跳转页面", SkipActivity.class);
        add("手机绑定", BindPhoneActivity.class);
        add("邮箱绑定", BindEmailActivity.class);
        add("修改密码", ChangePwdActivity.class);
        add("投稿平台", ContributePlatformActivity.class);

        //add("update notice dialog", view -> {});

        int[] A1 = new int[]{-1, 1, 3, 3, 3, 2, 1, 0};
        //solution1(A1);
        int[] A2 = new int[]{1, 3, -3};
        //solution2(A2);
    }

    public int solution1(int[] A) {
        int count = 0;
        for (int i = 0; i < A.length - 2; ++i) {
            int difference = A[i + 1] - A[i];
            for (int j = i + 1; j < A.length - 1; ++j) {
                if (difference == (A[j + 1] - A[j])) {
                    YSLog.d(TAG, i + " --- " + (j + 1));
                    count++;
                }
            }
        }
        YSLog.d(TAG, "count  = " + count);
        return count;
    }

    public int solution2(int[] A) {
        int min, max;
        min = A[0];
        max = A[0];
        for (int i = 1; i < A.length; ++i) {
            int a = A[i];
            if (a < min) {
                min = a;
            }
            if (a > max) {
                max = a;
            }
        }
        YSLog.d(TAG, "max = " + max + "   " + "min = " + min);
        YSLog.d(TAG, "max - min = " + (max - min));
        return max - min;
    }
}
