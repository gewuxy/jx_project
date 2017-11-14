package yy.doctor.ui.activity.home;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseListActivity;
import yy.doctor.R;
import yy.doctor.adapter.home.NoticeAdapter;
import yy.doctor.model.notice.Notice;
import yy.doctor.model.notice.Notice.TNotice;
import yy.doctor.model.notice.NoticeManager;
import yy.doctor.model.notice.NoticeNum;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivityRouter;
import yy.doctor.util.Util;

/**
 * 通知页面
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class NoticeActivity extends BaseListActivity<Notice, NoticeAdapter> {

    private List<Notice> mNotices;
    private TextView mTvEmpty;

    @Override
    public void initData(Bundle state) {
        //读取通知数据库的数据
        mNotices = NoticeManager.inst().queryAll();
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, getString(R.string.notice), this);
    }

    @Override
    public View createEmptyView() {
        return inflate(R.layout.layout_empty_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvEmpty = findView(R.id.empty_footer_tv);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvEmpty.setText(R.string.empty_notice);
        setData(mNotices);
        invalidate();
    }

    //点击了后要去掉小红点  数据库里状态改为已读
    @Override
    public void onItemClick(View v, int position) {

        Notice item = getItem(position);
        if (getAdapter().getCacheVH(position) != null) {
            hideView(getAdapter().getCacheVH(position).getIvDot());
        }

        YSLog.d(TAG, " msgType = " + item.getInt(TNotice.msgType));
        //判断是否有跳转事件  有就跳转会议详情页面
        if (item.getInt(TNotice.msgType) == 1) {
            MeetingDetailsActivityRouter.create(
                    item.getString(TNotice.meetId), item.getString(TNotice.meetName)
            ).route(this);
        } else {
            // do nothing
        }

        //判断消息是否读过
        if (!item.getBoolean(TNotice.is_read)) {
            item.put(TNotice.is_read, true);
            item.setContent(item.toJson());
            NoticeManager.inst().update(item);

            //未读消息数 -1
            NoticeNum.inst().reduce();
        }

        //判断消息是否都已经读完
        YSLog.d(TAG, " 小红点 = " + NoticeNum.inst().getCount());
        if (NoticeNum.inst().getCount() == 0) {
            notify(NotifyType.read_all_notice);
        }
    }

}
