package yy.doctor.activity;

import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.NoticeAdapter;
import yy.doctor.model.Notice;
import yy.doctor.model.Notice.TNotice;
import yy.doctor.model.NoticeManager;
import yy.doctor.model.NoticeSize;
import yy.doctor.util.Util;

/**
 * 通知页面
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class NoticeActivity extends BaseListActivity<Notice, NoticeAdapter> {

    private List<Notice> mNotices;

    @Override
    public void initData() {
        //读取通知数据库的数据
        mNotices = NoticeManager.inst().queryAll();
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "通知", this);
    }

    @Override
    public void setViews() {
        super.setViews();

        setData(mNotices);
    }

    //点击了后要去掉小红点  数据库里状态改为已读
    @Override
    public void onItemClick(View v, int position) {

        Notice item = getItem(position);
        if (getAdapter().getCacheVH(position) != null) {
            hideView(getAdapter().getCacheVH(position).getIvDot());
        }

        item.put(TNotice.is_read, true);
        item.setContent(item.toStoreJson());
        NoticeManager.inst().update(item);

        //未读消息数 -1
        if (NoticeSize.homeInst().size() > 0) {
            NoticeSize.homeInst().remove(0);
        }

        //判断是否有跳转事件  有就跳转会议详情页面
        if (item.getInt(TNotice.msgType) == 1) {
            MeetingDetailsActivity.nav(this, item.getString(TNotice.meetId));
        } else {
            // do nothing
        }
    }

    //判断消息是否都已经读完
    @Override
    protected void onDestroy() {

        //判断消息是否都已经读完
        if (NoticeSize.homeInst().size() > 0) {
            notify(NotifyType.receiver_notice);
        } else {
            notify(NotifyType.read_all_notice);
        }
        super.onDestroy();
    }

}
