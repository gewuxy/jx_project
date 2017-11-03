package jx.csp.contact;

import jx.csp.model.main.Meet;
import lib.yy.contract.IContract;

/**
 * @auther yuansui
 * @since 2017/11/1
 */

public interface MeetContract {

    interface V extends IContract.View {
    }

    interface P extends IContract.Presenter<V> {
        void onMeetClick(Meet item);

        void onShareClick(Meet item);

        void onLiveClick(Meet item);

        void allowJoin();

        void disagreeJoin();
    }
}
