package jx.csp.model;

import java.util.Observable;

import jx.csp.model.RecordUnusualState.TRecordUnusualState;
import jx.csp.sp.SpUser;
import lib.ys.impl.SingletonImpl;
import lib.ys.model.EVal;
import lib.ys.ui.interfaces.ISingleton;

/**
 * 记录录音的时候是否异常退出
 *
 * @author CaiXiang
 * @since 2018/1/4
 */

public class RecordUnusualState extends EVal<TRecordUnusualState> implements ISingleton {

    public enum TRecordUnusualState {
        unusualExit,   // 录音中是否异常退出
        courseId,  // 会议id
        page,  // 页数
        pageId,  // 对应的ppt id
    }

    private static RecordUnusualState mInst = null;

    public synchronized static RecordUnusualState inst() {
        if (mInst == null) {
            mInst = SpUser.inst().getEV(RecordUnusualState.class);
            if (mInst == null) {
                mInst = new RecordUnusualState();
                SingletonImpl.inst().addObserver(mInst);
            }
        }
        return mInst;
    }

    public boolean getUnusualExitState() {
        return getBoolean(TRecordUnusualState.unusualExit, false);
    }

    public void saveToSp() {
        SpUser.inst().save(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        mInst = null;
    }
}
