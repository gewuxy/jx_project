package yy.doctor.serv;

import android.content.Intent;

import org.json.JSONException;

import inject.annotation.router.Route;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.config.GlConfig;
import yy.doctor.model.config.GlConfigInfo;
import yy.doctor.model.config.GlConfigInfo.TGlConfigInfo;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.RegisterAPI;
import yy.doctor.sp.SpConfig;
import yy.doctor.sp.SpConfig.SpConfigKey;

/**
 * @auther : GuoXuan
 * @since : 2017/8/10
 */
@Route
public class GlConfigServ extends ServiceEx {

    @Override
    protected void onHandleIntent(Intent intent) {
        SpConfig spConfig = SpConfig.inst();

        int version = spConfig.getVersion();
        GlConfigInfo info = new GlConfigInfo();
        if (version == spConfig.KDefaultVersion) {
            // 初始(本地没有信息时)先从资源目录获取一次配置信息
            // 读取资源文件的内容
            info.parse(ResLoader.getRawContent(R.raw.gl_config));

            version = info.getInt(TGlConfigInfo.version);
            // 保存配置信息
            spConfig.saveInfo(info);
        } else {
            info.parse(SpConfig.inst().getString(SpConfigKey.KConfigHospitalLevels));
        }

        GlConfig.inst().setHospitalLevels(info.getList(TGlConfigInfo.propList));

        exeNetworkReq(RegisterAPI.config(version).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp resp) throws JSONException {
        return JsonParser.ev(resp.getText(), GlConfigInfo.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<GlConfigInfo> r = (Result) result;
        if (r.isSucceed()) {
            GlConfigInfo info = r.getData();
            if (info == null) {
                stopSelf();
                return;
            }

            int newVersion = info.getInt(TGlConfigInfo.version);
            int oldVersion = SpConfig.inst().getVersion();
            if (oldVersion < newVersion) {
                // 有更新保存最新信息
                SpConfig.inst().saveInfo(info);
                GlConfig.inst().setHospitalLevels(info.getList(TGlConfigInfo.propList));
                YSLog.d(TAG, "onNetworkSuccess:update");
            }
        }

        stopSelf();
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);
        stopSelf();
    }
}
