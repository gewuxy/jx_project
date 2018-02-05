package jx.csp.model.main.photo;

import jx.csp.constant.UploadType;
import lib.ys.model.EVal;

/**
 * 选择的照片
 *
 * @auther : GuoXuan
 * @since : 2018/2/5
 */
public class ChoicePhoto extends EVal<ChoicePhoto.TChoicePhoto> implements IUpload {

    @Override
    public int getType() {
        return UploadType.photo;
    }

    public enum TChoicePhoto {
        path,
    }

}
