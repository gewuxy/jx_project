package lib.ys.adapter.recycler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.SimpleOnItemTouchListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * RecyclerView的点击事件监听
 * <pre>
 * 有两种实现方式:
 * 1. 給每个item的root设置onClick和onLongClick监听事件
 * 2. 使用RecyclerView提供的OnItemTouchListener来自己做判断
 * 目前用的是第二种, 不过并没有按照网上的使用手势的Detector(无法让item响应press消息而使背景色改变)
 *
 * @deprecated FIXME: childView如果设置了点击事件后, 无法避免root也收到press消息, 会同时高亮, 所以保留这个类但是暂时不使用
 * @author yuansui
 */
public class RecyclerItemClickListener extends SimpleOnItemTouchListener {

    private static final int KWhatSetPressTrue = 0;
    private static final int KWhatSetPressFalse = 1;
    private static final int KWhatLongClick = 2;

    private RecyclerView mRecyclerView;
    private View mChildView;

    private OnRecyclerItemClickListener mListener;
    private boolean mEnableLongClick;
    private Handler mHandler;
    private boolean mInLongClick;
    private boolean mScrolled;

    public RecyclerItemClickListener(@NonNull final OnRecyclerItemClickListener lsn, final boolean enableLongClick) {
        if (lsn == null) {
            return;
        }
        mListener = lsn;
        mEnableLongClick = enableLongClick;

        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case KWhatSetPressTrue: {
                        if (mChildView != null) {
                            mChildView.setPressed(true);
                        }
                    }
                    break;
                    case KWhatLongClick: {
                        if (mChildView != null) {
                            mListener.onItemLongClick(mChildView, mRecyclerView.getChildLayoutPosition(mChildView));
                            mChildView.setPressed(false);
                            mInLongClick = true;
                            mChildView = null;
                        }
                    }
                    break;
                    case KWhatSetPressFalse: {
                        if (mChildView != null) {
                            mChildView.setPressed(false);
                            mChildView = null;
                        }
                    }
                    break;
                }
            }
        };
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        if (mListener == null) {
            return false;
        }

        if (mRecyclerView == null) {
            mRecyclerView = recyclerView;
            mRecyclerView.addOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    mScrolled = true;
                    mHandler.removeMessages(KWhatLongClick);
                    if (mChildView != null) {
                        mChildView.setPressed(false);
                        mChildView = null;
                    }
                }
            });
        }

        View v = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (v != null) {
            if (mChildView == null) {
                mChildView = v;
            }

            int action = MotionEventCompat.getActionMasked(e);

            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mInLongClick = false;
                    mScrolled = false;

                    mHandler.sendEmptyMessageDelayed(KWhatSetPressTrue, 100);
                    if (mEnableLongClick) {
                        mHandler.sendEmptyMessageDelayed(KWhatLongClick, 1000);
                    }
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    if (mChildView != null) {
                        if (mChildView != v) {
                            mHandler.removeMessages(KWhatSetPressTrue);
                            mHandler.removeMessages(KWhatLongClick);
                            mChildView.setPressed(false);
                            mScrolled = true;
                        }
                    }
                }
                break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    if (mScrolled) {
                        break;
                    }

                    if (!mInLongClick && mChildView == v) {
                        mListener.onItemClick(v, mRecyclerView.getChildLayoutPosition(v));
                    }
                    mHandler.removeMessages(KWhatLongClick);

                    if (mHandler.hasMessages(KWhatSetPressTrue)) {
                        // 还未显示press变色, 等待
                        mHandler.sendEmptyMessageDelayed(KWhatSetPressFalse, 100);
                    } else {
                        v.setPressed(false);
                        mChildView = null;
                    }
                }
                break;
            }
        }
        return false;
    }
}