package jx.csp.ui.frag.record;

import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import java.io.File;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.constant.AudioType;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import lib.jx.ui.frag.base.BaseFrag;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.shape.CornerRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;

/**
 * @author CaiXiang
 * @since 2017/9/30
 */
@Route
public class RecordImgFrag extends BaseFrag {

    private NetworkImageView mIv;
    private ImageView mIvBg;

    @Arg
    String mImgUrl;

    // 录播时，判断音频文件是否已经存在
    @Arg(opt = true)
    String mAudioFilePath;

    @Arg(opt = true)
    String mAudioUrl;

    @Arg(opt = true)
    boolean mModified;  // 是否需要重新下载音频

    private float mCrown; // 图片的宽高比
    private int mIvHeight;  // 图片高度
    private int mIvWidth;  // 图片宽度

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_record_img;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mIv = findView(R.id.frag_record_img_iv);
        mIvBg = findView(R.id.frag_record_img_iv_bg);
    }

    @Override
    public void setViews() {
        mIv.renderer(new CornerRenderer(fit(5)))
                .url(mImgUrl)
                .listener(new NetworkImageListener() {
                    @Override
                    public void onImageSet(ImageInfo info) {
                        int height = info.getHeight();
                        int width = info.getWidth();
                        YSLog.d(TAG, "ppt width = " + width + "ppt height = " + height);
                        if (width > height) {
                            mIvWidth = 332;
                            mCrown = (width * 1.0f) / height;
                            mIvHeight = (int) (mIvWidth / mCrown);
                            YSLog.d(TAG, "换算后的 height = " + mIvHeight);
                            if (mIvHeight > 246) {
                                mIvHeight = 246;
                            }
                        } else {
                            mIvHeight = 246;
                            mCrown = (height * 1.0f) / width;
                            mIvWidth = (int) (mIvHeight / mCrown);
                            YSLog.d(TAG, "换算后的 width = " + mIvWidth);
                            if (mIvWidth > 332) {
                                mIvHeight = 332;
                            }
                        }
                        LayoutParams params = (LayoutParams) mIv.getLayoutParams();
                        params.width = fit(mIvWidth);
                        params.height = fit(mIvHeight);
                        mIv.setLayoutParams(params);
                        goneView(mIvBg);
                    }
                })
                .resize(fit(mIvWidth), fit(mIvHeight))
                .load();
        // 判断是否上传过音频，如果上传过再判断音频文件是否还存在，不存在就下载，下载下来的是mp3文件,下载完成显示播放按钮
        if (TextUtil.isNotEmpty(mAudioUrl)) {
            // 文件有可能是amr或者mp3文件
            String amrFilePath;
            String mp3FilePath = null;
            File f_amr = null;
            File f_mp3 = null;
            if (mAudioFilePath.contains(AudioType.amr)) {
                amrFilePath = mAudioFilePath;
                mp3FilePath = mAudioFilePath.replace(AudioType.amr, AudioType.mp3);
                f_amr = new File(amrFilePath);
                f_mp3 = new File(mp3FilePath);
            }
            if (mAudioFilePath.contains(AudioType.mp3)) {
                mp3FilePath = mAudioFilePath;
                amrFilePath = mAudioFilePath.replace(AudioType.mp3, AudioType.amr);
                f_amr = new File(amrFilePath);
                f_mp3 = new File(mp3FilePath);
            }
            String filePath = mp3FilePath.substring(0, mAudioFilePath.lastIndexOf(File.separator));
            String fileName = mp3FilePath.substring(mAudioFilePath.lastIndexOf(File.separator) + 1);
            if (f_amr.exists() || f_mp3.exists()) {
                YSLog.d(TAG, "audio already download");
                if (mModified) {
                    YSLog.d(TAG, "audio change, download again");
                    // 先删除原来的文件
                    FileUtil.delFile(f_amr);
                    exeNetworkReq(MeetingAPI.downloadAudio(filePath, fileName, mAudioUrl).build());
                }
            } else {
                YSLog.d(TAG, "download mp3 file, file path = " + mp3FilePath);
                exeNetworkReq(MeetingAPI.downloadAudio(filePath, fileName, mAudioUrl).build());
            }
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        super.onNetworkSuccess(id, r);
    }
}
