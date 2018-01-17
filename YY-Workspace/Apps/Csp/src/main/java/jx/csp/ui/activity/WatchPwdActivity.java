package jx.csp.ui.activity;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.meeting.WatchPwd;
import jx.csp.model.meeting.WatchPwd.TWatchPwd;
import jx.csp.network.JsonParser;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * @auther ${HuoXuYu}
 * @since 2018/1/3
 */
@Route
public class WatchPwdActivity extends BaseActivity {
    private final int KHaveWatchPwd = 0;
    private final int KNoWatchPwd = 1;

    private LinearLayout mLayoutPwd;
    private EditText mEtPwd;
    private TextView mTvConfirm;
    private TextView mTvPwd;
    private TextView mTvTips;

    @Arg
    int mCourseId;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_watch_pwd;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.watch_setting_watch_pwd, this);
    }

    @Override
    public void findViews() {
        mLayoutPwd = findView(R.id.watch_pwd_layout_pwd);
        mEtPwd = findView(R.id.watch_pwd_et_pwd);

        mTvConfirm = findView(R.id.watch_pwd_tv_confirm);
        mTvPwd = findView(R.id.watch_pwd_tv_pwd);
        mTvTips = findView(R.id.watch_pwd_tv_tips);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.watch_pwd_tv_confirm);
        setOnClickListener(R.id.watch_pwd_tv_create);

        //密码输入格式
        getPwdChange();
        //判断会议是否有密码
        mTvPwd.setText(Profile.inst().getWatchPwd(mCourseId));
        if (TextUtil.isNotEmpty(mTvPwd.getText().toString())) {
            getPwdSetting();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.watch_pwd_tv_confirm: {
                // FIXME: 2018/1/4 测试代码,不是真实逻辑,点击事件为接口
                if (TextUtil.isNotEmpty(getPwd())) {
                    //无密码,点击为确认并保存
                    List<WatchPwd> list = Profile.inst().getList(TProfile.watchPwdList);
                    if (list == null) {
                        list = new ArrayList<>();
                    }

                    boolean flag = true;
                    for (WatchPwd pwd : list) {
                        if (pwd.getInt(TWatchPwd.id) == mCourseId) {
                            pwd.put(TWatchPwd.pwd, getPwd());
                            flag = false;
                        }
                    }

                    if (flag) {
                        WatchPwd pwd = new WatchPwd();
                        pwd.put(TWatchPwd.id, mCourseId);
                        pwd.put(TWatchPwd.pwd, getPwd());
                        list.add(pwd);
                    }

                    Profile.inst().put(TProfile.watchPwdList, list);
                    Profile.inst().saveToSp();

                    mTvPwd.setText(getPwd());
                    mEtPwd.setText(ConstantsEx.KEmpty);
                    getPwdSetting();
                } else {
                    //有密码,点击为删除
                    List<WatchPwd> list = Profile.inst().getList(TProfile.watchPwdList);
                    if (list == null) {
                        list = new ArrayList<>();
                    }

                    boolean flag = true;
                    for (WatchPwd pwd : list) {
                        if (pwd.getInt(TWatchPwd.id) == mCourseId) {
                            list.clear();
                            flag = false;
                        }
                    }

                    if (flag) {
                        WatchPwd pwd = new WatchPwd();
                        pwd.put(TWatchPwd.id, ConstantsEx.KEmpty);
                        pwd.put(TWatchPwd.pwd, ConstantsEx.KEmpty);
                        list.add(pwd);
                    }

                    Profile.inst().put(TProfile.watchPwdList, list);
                    Profile.inst().saveToSp();

                    mTvPwd.setText(ConstantsEx.KEmpty);
                    getUnPwdSetting();
                }
            }
            break;
            case R.id.watch_pwd_tv_create: {
                mEtPwd.setText(getStringRandom(8));
                mEtPwd.setSelection(getPwd().length());
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        switch (id) {
            case KHaveWatchPwd: {
                //设置密码
                if (r.isSucceed()) {
                    List<WatchPwd> list = Profile.inst().getList(TProfile.watchPwdList);
                    if (list == null) {
                        list = new ArrayList<>();
                    }

                    boolean flag = true;
                    for (WatchPwd pwd : list) {
                        if (pwd.getInt(TWatchPwd.id) == mCourseId) {
                            pwd.put(TWatchPwd.pwd, getPwd());
                            flag = false;
                        }
                    }

                    if (flag) {
                        WatchPwd pwd = new WatchPwd();
                        pwd.put(TWatchPwd.id, mCourseId);
                        pwd.put(TWatchPwd.pwd, getPwd());
                        list.add(pwd);
                    }

                    Profile.inst().put(TProfile.watchPwdList, list);
                    Profile.inst().saveToSp();

                    mTvPwd.setText(getPwd());
                    mEtPwd.setText(ConstantsEx.KEmpty);
                    getPwdSetting();
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KNoWatchPwd: {
                //删除密码
                if (r.isSucceed()) {
                    List<WatchPwd> list = Profile.inst().getList(TProfile.watchPwdList);
                    if (list == null) {
                        list = new ArrayList<>();
                    }

                    boolean flag = true;
                    for (WatchPwd pwd : list) {
                        if (pwd.getInt(TWatchPwd.id) == mCourseId) {
                            list.clear();
                            flag = false;
                        }
                    }

                    if (flag) {
                        WatchPwd pwd = new WatchPwd();
                        pwd.put(TWatchPwd.id, ConstantsEx.KEmpty);
                        pwd.put(TWatchPwd.pwd, ConstantsEx.KEmpty);
                        list.add(pwd);
                    }

                    Profile.inst().put(TProfile.watchPwdList, list);
                    Profile.inst().saveToSp();

                    mTvPwd.setText(ConstantsEx.KEmpty);
                    getUnPwdSetting();
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
        }
    }

    public String getPwd() {
        return mEtPwd.getText().toString();
    }

    /**
     * @return true为点击空白处收起键盘
     */
    @Override
    protected boolean enableHideKeyboardWhenChangeFocus() {
        return true;
    }

    /**
     * 设置有密码的界面
     */
    private void getPwdSetting() {
        goneView(mLayoutPwd);
        showView(mTvPwd);

        mTvTips.setText(R.string.watch_tips2);
        mTvConfirm.setText(R.string.watch_delete_pwd);
        mTvConfirm.setEnabled(true);
    }

    /**
     * 设置无密码界面,须输入
     */
    private void getUnPwdSetting() {
        goneView(mTvPwd);
        showView(mLayoutPwd);

        mTvTips.setText(R.string.watch_tips);
        mTvConfirm.setText(R.string.confirm);
        mTvConfirm.setEnabled(false);
    }

    /**
     * 生成随机数
     *
     * @param length 生成length位随机数
     * @return
     */
    public String getStringRandom(int length) {
        String val = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                //字母的ASCII码 : 65是大写A, 97是小写a
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 密码输入格式
     */
    private void getPwdChange() {
        UISetter.setWatchPwdRange(mEtPwd);
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3 && s.length() < 9) {
                    mTvConfirm.setEnabled(true);
                } else {
                    mTvConfirm.setEnabled(false);
                }
            }
        });
    }
}
