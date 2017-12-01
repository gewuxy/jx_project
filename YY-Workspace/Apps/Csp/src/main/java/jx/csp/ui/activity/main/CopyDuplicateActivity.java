package jx.csp.ui.activity.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.meeting.Copy;
import jx.csp.model.meeting.Copy.TCopy;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;

/**
 * 复制副本
 *
 * @author CaiXiang
 * @since 2017/11/24
 */
@Route
public class CopyDuplicateActivity extends BaseActivity {

    @Arg
    String mTitle;
    @Arg
    String mCourseId;

    private EditText mEt;
    private TextView mTv;

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_copy_duplicate;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.drawable.default_ic_close, this);
        bar.addTextViewMid(R.string.copy_ectype);
    }

    @Override
    public void findViews() {
        mEt = findView(R.id.copy_ectype_et);
        mTv = findView(R.id.copy_ectype_tv);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.copy_ectype_tv);
        mEt.setText(mTitle);
        mEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    mTv.setEnabled(true);
                } else {
                    mTv.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        refresh(RefreshWay.dialog);
        exeNetworkReq(MeetingAPI.copy(mCourseId, mEt.getText().toString()).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Copy.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            if (r.isSucceed()) {
                Copy copy = (Copy) r.getData();
                copy.put(TCopy.oldId, mCourseId);
                copy.put(TCopy.title, mEt.getText().toString());
                Notifier.inst().notify(NotifyType.copy_duplicate, copy);
                YSLog.d(TAG, mCourseId + "发送复制通知");
            } else {
                onNetworkError(id, r.getError());
            }
        }
        finish();
    }
}
