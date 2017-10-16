package jx.csp.ui.activity.record;

import jx.csp.ui.activity.record.RecordContract.RecordPresenter;
import jx.csp.ui.activity.record.RecordContract.RecordView;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @author CaiXiang
 * @since 2017/10/11
 */

public class LiveRecordPresenterImpl implements RecordPresenter, OnCountDownListener {

    private RecordView mRecordView;
    private CountDown mCountDown;

    public LiveRecordPresenterImpl(RecordView recordView) {
        mRecordView = recordView;
    }

    @Override
    public void startCountDown(long startTime, long stopTime) {

    }

    @Override
    public void startRecord(String filePath) {

    }

    @Override
    public void stopRecord() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onCountDown(long remainCount) {

    }

    @Override
    public void onCountDownErr() {
    }
}
