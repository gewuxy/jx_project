package jx.csp.model.main.photo;

import jx.csp.constant.UploadType;

/**
 * 相机类型
 *
 * @auther : GuoXuan
 * @since : 2018/2/5
 */
public class ChoiceCamera implements IUpload {

    @Override
    public int getType() {
        return UploadType.camera;
    }

}
