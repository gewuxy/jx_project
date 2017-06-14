package yy.doctor.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import lib.ys.model.EVal;
import yy.doctor.model.Notice.TNotice;

/**
 * @auther yuansui
 * @since 2017/6/12
 */

@Entity(active = true, nameInDb = "notice")
public class Notice extends EVal<TNotice> {

    public enum TNotice {
        id,
        time,
        from,
        content,
        is_read,
        msgType,
        meetId,
    }

    @Id
    private Long id;

    @Property(nameInDb = "content")
    private String content;

    @Property(nameInDb = "user_id")
    private String uid;

    @Property(nameInDb = "timestamp")
    private long timestamp;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 951252399)
    private transient NoticeDao myDao;

    @Generated(hash = 1292174379)
    public Notice(Long id, String content, String uid, long timestamp) {
        this.id = id;
        this.content = content;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    @Generated(hash = 1880392847)
    public Notice() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1512276729)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoticeDao() : null;
    }

}
