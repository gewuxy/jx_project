package jx.csp.model.main.photo;

import jx.csp.constant.UploadType;

/**
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
public interface IUpload {

    @UploadType
    int getType();

}
