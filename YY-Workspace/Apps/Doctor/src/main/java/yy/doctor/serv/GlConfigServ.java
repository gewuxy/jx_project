package yy.doctor.serv;

import android.content.Intent;

import org.json.JSONException;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.processor.annotation.AutoIntent;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.GlConfigInfo;
import yy.doctor.model.GlConfigInfo.TGlConfigInfo;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpConfig;

/**
 * @auther : GuoXuan
 * @since : 2017/8/10
 */
@AutoIntent
public class GlConfigServ extends ServiceEx {
// FIXME:错误提示
    @Override
    protected void onHandleIntent(Intent intent) {
        SpConfig inst = SpConfig.inst();
        int version = inst.getVersion();
        if (ConstantsEx.KErrNotFound == version) {
            // 初始(本地没有信息时)先从资源目录获取一次配置信息
            // 读取资源文件的内容
            GlConfigInfo info = new GlConfigInfo();
            info.parse(ResLoader.getRawContent(R.raw.gl_config));

            version = info.getInt(TGlConfigInfo.version);
            // 保存配置信息
            inst.saveInfo(info);
        }
        exeNetworkReq(NetFactory.config(version));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws JSONException {
        return JsonParser.ev(r.getText(), GlConfigInfo.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<GlConfigInfo> r = (Result) result;
        if (r.isSucceed()) {
            GlConfigInfo info = r.getData();
            int newVersion = info.getInt(TGlConfigInfo.version);
            int oldVersion = SpConfig.inst().getVersion();
            if (oldVersion < newVersion) {
                // 有更新保存最新信息
                SpConfig.inst().saveInfo(info);
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
