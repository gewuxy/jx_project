package jx.csp.presenter;

import jx.csp.constant.WatchPwdType;
import jx.csp.contact.WatchPwdContract;
import jx.csp.contact.WatchPwdContract.V;
import jx.csp.model.meeting.MeetPwd;
import jx.csp.model.meeting.MeetPwd.TMeetPwd;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TextUtil;

/**
 * @auther HuoXuYu
 * @since 2018/1/27
 */

public class WatchPwdPresenterImpl extends BasePresenterImpl<WatchPwdContract.V> implements WatchPwdContract.P {

    private final int KNoWatchPwd = 1;
    private final int KExistingWatchPwd = 2;
    private final int KGetPassword = 3;

    private String mPassword;

    public WatchPwdPresenterImpl(V v) {
        super(v);
    }

    @Override
    public void getPassword(String id, String courseId, String password) {
        mPassword = password;
        switch (id) {
            case WatchPwdType.setPwd: {
                exeNetworkReq(KNoWatchPwd, MeetingAPI.setPassword(courseId, WatchPwdType.setPwd, password).build());
            }
            break;
            case WatchPwdType.delete: {
                exeNetworkReq(KExistingWatchPwd, MeetingAPI.setPassword(courseId, WatchPwdType.delete, password).build());
            }
            break;
            case WatchPwdType.getPwd: {
                exeNetworkReq(KGetPassword, MeetingAPI.getPassword(courseId).build());
            }
            break;
        }
    }

    @Override
    public String getRandomPwd(int length) {
        String val = ConstantsEx.KEmpty;
        for (int i = 0; i < length; i++) {
            val += String.valueOf((int) (Math.random() * 10));
        }
        return val;
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        switch (id) {
            case KNoWatchPwd: {
                //设置密码
                if (r.isSucceed()) {
                    getView().setPwdText(mPassword);
                    getView().setExistingPwd();
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KExistingWatchPwd: {
                //删除密码
                if (r.isSucceed()) {
                    getView().setUnPwd();
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KGetPassword: {
                if (r.isSucceed()) {
                    getView().setViewState(ViewState.normal);
                    MeetPwd pwd = (MeetPwd) r.getData();
                    String password = pwd.getString(TMeetPwd.password);
                    if (TextUtil.isEmpty(password)) {
                        getView().setUnPwd();
                    } else {
                        getView().setPwdText(password);
                        getView().setExistingPwd();
                    }
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KGetPassword) {
            return JsonParser.ev(resp.getText(), MeetPwd.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        if (id == KGetPassword) {
            getView().setViewState(ViewState.error);
        }
    }


}
