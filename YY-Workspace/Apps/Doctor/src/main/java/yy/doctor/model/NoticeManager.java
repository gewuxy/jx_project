package yy.doctor.model;

import java.util.List;

import yy.doctor.App;
import yy.doctor.model.Notice.TNotice;
import yy.doctor.model.NoticeDao.Properties;
import yy.doctor.model.Profile.TProfile;

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
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(App.getContext(), "notice.db", null);
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
        item.setContent(item.toStoreJson());
        item.setUid(Profile.inst().getString(TProfile.username));
        item.setTimestamp(item.getLong(TNotice.time));
    }

    public List<Notice> queryAll() {
        List<Notice> notices = mDao.queryBuilder()
                .where(Properties.Uid.eq(Profile.inst().getString(TProfile.username)))
                .orderDesc(Properties.Timestamp)
                .list();

        for (Notice notice : notices) {
            notice.set(notice.getContent());
        }

        return notices;
    }
}
