package jx.doctor.model.unitnum;

import lib.ys.model.EVal;
import jx.doctor.model.unitnum.File.TFile;

/**
 * Created by CaiXiang on 2017/5/11.
 */

public class File extends EVal<TFile> {

    public enum TFile {

        id,  //资料id
        fileSize, // 文件大小, 单位: byte
        createTime,

        materialName,   //资料名称
        materialUrl,   //资料url
        materialType,    //资料类型

        name, //  文件名
        fileUrl,  //  下载地址
        fileType, //  文件类型
        fileSizeStr, //  文件大小
        meetId, // 会议id
        userId, //  单位号id

        htmlUrl, //  会议资料html路径
    }

}
