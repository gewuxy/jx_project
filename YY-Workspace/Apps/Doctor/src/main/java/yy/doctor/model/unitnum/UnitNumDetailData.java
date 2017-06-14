package yy.doctor.model.unitnum;

import lib.ys.model.EVal;
import yy.doctor.model.unitnum.UnitNumDetailData.TUnitNumDetailData;

/**
 * Created by XPS on 2017/5/11.
 */

public class UnitNumDetailData extends EVal<TUnitNumDetailData> {

    public enum TUnitNumDetailData {

        id,  //资料id
        fileSize, // 文件大小
        createTime,

        materialName,   //资料名称
        materialUrl,   //资料url
        materialType,    //资料类型

        name, //  文件名
        fileUrl,  //  下载地址
        fileType , //  文件类型
        fileSizeStr, //  文件大小
        meetId, // 会议id
        userId, //  ？
    }

}
