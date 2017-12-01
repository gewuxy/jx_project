package jx.doctor;

import android.support.annotation.IntDef;

public interface Extra {
    String KData = "data";
    String KName = "name";
    String KType = "type";
    String KUrl = "url";
    String KLimit = "limit";

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

    @IntDef({
            FileFrom.meeting,
            FileFrom.unit_num,
    })
    @interface FileFrom {
        int meeting = 0;
        int unit_num = 1;
    }
}
