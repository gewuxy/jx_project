package jx.csp.contact;

import android.util.SparseArray;

import jx.csp.model.meeting.Course.CourseType;
import lib.jx.contract.IContract;

/**
 * @author CaiXiang
 * @since 2017/11/16
 */

public interface AudioUploadContract {

    interface V extends IContract.View {
    }

    interface P extends IContract.Presenter<AudioUploadContract.V> {

        void setCourseDetailIdArray(SparseArray<String> courseDetailIdArray);

        void uploadAudioFile(String courseId, int page, @CourseType int type, String audioFilePath, int time);
    }
}
