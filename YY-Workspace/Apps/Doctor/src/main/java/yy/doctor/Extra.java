package yy.doctor;

import android.support.annotation.IntDef;

/**
 * Created by Administrator on 2017/3/30.
 */

public interface Extra {
    String KData = "data";
    String KName = "name";
    String KType = "type";
    String KUrl = "url";

    String KTitle = "title";
    String KLeaf = "leaf";

    String KId = "id";
    String KUnitNumId = "unitNumId";
    String KColor = "color";
    String KFilePath = "filePath";
    String KPage = "page";
    String KNum = "num";
    String KSubmit = "submit";

    String KPass = "pass";
    String KPaperId = "paperId";
    String KMeetId = "meetId";
    String KModuleId = "moduleId";

    String KLatitude = "latitude"; // 维度
    String KLongitude = "longitude"; // 经度

    String KPcdDesc = "pcd_desc";
    String KProvince = "province";
    String KProvinceId = "province_id";
    String KCity = "city";
    String KCityId = "city_id";
    String KDistrict = "district";

    @IntDef({
            FileFrom.meeting,
            FileFrom.unit_num,
    })
    @interface FileFrom {
        int meeting = 0;
        int unit_num = 1;
    }
}
