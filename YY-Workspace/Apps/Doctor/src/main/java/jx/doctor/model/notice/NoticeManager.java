package jx.doctor.model.notice;

import java.util.List;

import jx.doctor.App;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.notice.DaoMaster.DevOpenHelper;
import jx.doctor.model.notice.Notice.TNotice;
import jx.doctor.model.notice.NoticeDao.Properties;

/**
 * 通知数据库的管理类
 *
 * @author CaiXiang
 * @since 2017/6/14
 */

public class NoticeManager {

    private NoticeDao mDao;

    private static NoticeManager mInst = null;

    private NoticeManager() {
        DevOpenHelper devOpenHelper = new DevOpenHelper(App.getContext(), "contribution_invited.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        mDao = daoMaster.newSession().getNoticeDao();
    }

    public synchronized static NoticeManager inst() {
        if (mInst == null) {
            mInst = new NoticeManager();
        }
        return mInst;
    }

    public void insert(Notice item) {
        setVal(item);
        mDao.insert(item);
    }

    public void update(Notice item) {
        setVal(item);
        mDao.update(item);
    }

    private void setVal(Notice item) {
        item.setContent(item.toJson());
        item.setUid(Profile.inst().getString(TProfile.username));
        item.setTimestamp(item.getLong(TNotice.time));
    }

    public List<Notice> queryAll() {
        List<Notice> notices = mDao.queryBuilder()
                .where(Properties.Uid.eq(Profile.inst().getString(TProfile.username)))
                .orderDesc(Properties.Timestamp)
                .list();

        for (Notice notice : notices) {
            notice.parse(notice.getContent());
        }

        return notices;
    }
}
