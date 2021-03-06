package lib.jx.notify;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.jx.notify.Notifier.OnNotify;
import lib.ys.model.NotifierEx;

/**
 * 全局通知, 支持任意行为和数据
 *
 * @author yuansui
 */

public class Notifier extends NotifierEx<OnNotify> {

    @IntDef({
            NotifyType.un_know,

            NotifyType.login,
            NotifyType.logout,

            NotifyType.section_change,
            NotifyType.profile_change,
            NotifyType.unit_num_attention_change,
            NotifyType.receiver_notice,
            NotifyType.read_all_notice,
            NotifyType.token_out_of_date,
            NotifyType.cancel_attention,
            NotifyType.data_finish,

            NotifyType.study_start,
            NotifyType.study_end,

            NotifyType.collection_cancel_meeting,
            NotifyType.collection_cancel_drug,
            NotifyType.collection_cancel_clinic,
            NotifyType.collection_cancel_thomson,

            NotifyType.pcd_selected,
            NotifyType.fetch_message_captcha,
            NotifyType.disable_fetch_message_captcha,

            NotifyType.cme_num,
            NotifyType.section,
            NotifyType.certification,
            NotifyType.academic,
            NotifyType.hospital_finish,
            NotifyType.bind_wx,
            NotifyType.bind_phone,
            NotifyType.bind_email,
            NotifyType.bind_yaya,
            NotifyType.bind_fackbook,
            NotifyType.bind_sina,
            NotifyType.bind_twitter,
            NotifyType.delete_meeting_success,
            NotifyType.delete_meeting_fail,
            NotifyType.over_live,
            NotifyType.start_live,

            NotifyType.login_video,
            NotifyType.meet_num,
            NotifyType.total_time,

            NotifyType.finish_editor_meet,
            NotifyType.main_refresh,
            NotifyType.finish_preview,
            NotifyType.theme_refresh,

            NotifyType.comment_num,

            NotifyType.finish_contribute,

            NotifyType.course_already_delete_record,
            NotifyType.course_already_delete_main,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotifyType {

        int un_know = -1;

        /**
         * 登录成功
         * data = null
         */
        int login = 0;
        /**
         * 登出
         */
        int logout = 1;

        /**
         * 科室改变
         */
        int section_change = 3;

        /**
         * 个人资料更新
         */
        int profile_change = 4;

        /**
         * 单位号关注改变
         */
        int unit_num_attention_change = 5;

        /**
         * 收到通知
         */
        int receiver_notice = 6;

        /**
         * 通知都已经读完
         */
        int read_all_notice = 7;

        /**
         * token过期
         */
        int token_out_of_date = 8;

        /**
         * 取消关注  单位号
         */
        int cancel_attention = 9;

        /**
         * 开始学习
         */
        int study_start = 10;
        /**
         * 学习结束
         */
        int study_end = 11;

        /**
         * 数据中心页面结束
         */
        int data_finish = 12;

        /**
         * 结束省份页面
         */
        int pcd_selected = 14;

        /**
         * 提示获取验证码
         */
        int fetch_message_captcha = 15;

        /**
         * 提示不能获取验证码
         */
        int disable_fetch_message_captcha = 16;
        /**
         * 验证码按钮变倒计时
         */

        /**
         * CME卡号
         */
        int cme_num = 17;

        /**
         * 科室
         */
        int section = 18;

        /**
         * 职业资格证号
         */
        int certification = 19;

        /**
         * 学术专长
         */
        int academic = 20;

        int hospital_finish = 21;

        /**
         * 绑定成功
         */
        int bind_wx = 22;
        int bind_phone = 23;
        int bind_email = 24;
        int bind_yaya = 25;
        int bind_sina = 26;
        int bind_fackbook = 27;
        int bind_twitter = 28;


        /**
         * 取消 收藏会议
         */
        int collection_cancel_meeting = 30;
        /**
         * 取消药品收藏
         */
        int collection_cancel_drug = 31;
        /**
         * 取消 临床收藏
         */
        int collection_cancel_clinic = 32;
        /**
         * 取消汤森路透收藏
         */
        int collection_cancel_thomson = 33;

        /**
         * 删除会议成功
         */
        int delete_meeting_success = 34;

        /**
         * 删除会议失败
         */
        int delete_meeting_fail = 35;

        /**
         * 结束直播成功
         */
        int over_live = 37;

        /**
         * 开始直播
         */
        int start_live = 38;

        int login_video = 39;

        int meet_num = 40;

        int total_time = 41;

        /**
         * 结束PPT编辑页面
         */
        int finish_editor_meet = 50;
        int main_refresh = 51;
        int finish_preview = 52;
        int theme_refresh = 53;

        int comment_num = 54;

        /**
         * 结束投稿相关页面
         */
        int finish_contribute = 58;

        /**
         * 会议已经删除 针对音频直播、录播页面
         */
        int course_already_delete_record = 60;
        /**
         * 会议已经删除 针对首页
         */
        int course_already_delete_main = 61;
    }

    public interface OnNotify {
        void onNotify(@NotifyType int type, Object data);
    }

    private static Notifier mInst = null;

    public static Notifier inst() {
        if (mInst == null) {
            synchronized (Notifier.class) {
                if (mInst == null) {
                    mInst = new Notifier();
                }
            }
        }
        return mInst;
    }

    @Override
    protected void callback(OnNotify o, @NotifyType int type, Object data) {
        o.onNotify(type, data);
    }

}
