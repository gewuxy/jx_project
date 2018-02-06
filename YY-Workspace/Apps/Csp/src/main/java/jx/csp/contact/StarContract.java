package jx.csp.contact;

import jx.csp.model.meeting.Code;
import lib.jx.contract.IContract;

/**
 * @auther : GuoXuan
 * @since : 2018/1/19
 */
public interface StarContract {

    interface V extends IContract.View {

        void setReb(boolean reb);

        void onNetworkSuccess(Code c);

        void deleteBgMusicSuccess();
    }

    interface P extends IContract.Presenter<V> {

        void setPlayState();

        void getDataFromNet();

        void deleteBgMusic();
    }
}
