package yy.doctor.adapter.meeting;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.facebook.imageutils.BitmapUtil;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.fitter.DpFitter;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.util.DeviceUtil;
import lib.ys.util.FileUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.CourseVH;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.CourseType;
import yy.doctor.model.meet.ppt.Course.TCourse;

/**
 * 录播ppt缩略图的Apadter
 *
 * @auther : GuoXuan
 * @since : 2017/9/29
 */
public class MeetingBreviaryAdapter extends RecyclerAdapterEx<Course, CourseVH> {

    private final int KW = 129;
    private final int KH = 96;

    private ImageView mIvVideo;

    @Override
    protected void refreshView(int position, CourseVH holder) {
        int type = getItem(position).getType();
        ImageView ivVideo = holder.getIvVideo();
        NetworkImageView ivPPT = holder.getIvPPT();
        if (type != CourseType.video) {
            // 复用重置
            goneView(ivVideo);
            showView(ivPPT);
        }
        switch (type) {
            case CourseType.video: {
                if (ivVideo != mIvVideo) {
                    // 同一个不刷新
                    ivVideo.setImageResource(R.drawable.ic_default_breviary_video);
                    mIvVideo = ivVideo;

                    Observable.fromCallable(() -> getFirstFrame(position))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bitmap -> {
                                if (bitmap == null) {
                                    return;
                                }
                                ivVideo.setImageBitmap(bitmap);
                            });
                }

                showView(ivVideo);
                goneView(ivPPT);
            }
            break;
            case CourseType.audio: {
                ivPPT.res(R.drawable.ic_default_breviary_audio)
                        .load();
            }
            break;
            case CourseType.pic_audio:
            case CourseType.pic: {
                ivPPT.res(R.drawable.ic_default_breviary_image)
                        .url(getItem(position).getString(TCourse.imgUrl))
                        .resize(DpFitter.dp(KW), DpFitter.dp(KH))
                        .load();
            }
            break;
        }

        if (getItem(position).getBoolean(TCourse.select)) {
            setView(position);
        } else {
            goneView(holder.getLayoutMedia());
        }

        if (getItem(position).getBoolean(TCourse.play)) {
            nativeAnimation(holder, true);
        } else {
            nativeAnimation(holder, false);
        }
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_ppt_breviary_item;
    }

    @Nullable
    private Bitmap getFirstFrame(int position) {
        if (!DeviceUtil.isNetworkEnabled()) {
            return null;
        }
        MediaMetadataRetriever r = new MediaMetadataRetriever();
        r.setDataSource(getItem(position).getString(TCourse.videoUrl), new HashMap());
        Bitmap bitmap = r.getFrameAtTime(1);
        r.release();
        return bitmap;
    }

    private void nativeAnimation(CourseVH holder, boolean state) {
        Drawable drawable = holder.getIvMedia().getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) drawable;
            if (state == animation.isRunning()) {
                return;
            }
            if (state) {
                animation.start();
            } else {
                animation.stop();
            }
        }
    }

    private void setView(int position) {
        CourseVH holder = getCacheVH(position);
        int type = getItem(position).getType();
        switch (type) {
            case CourseType.audio:
            case CourseType.pic_audio: {
                showView(holder.getLayoutMedia());
                holder.getTvMedia().setText(getItem(position).getString(TCourse.time, "音频"));
                holder.getIvMedia().setImageResource(R.drawable.animation_audio);
            }
            break;
            case CourseType.pic: {
                // do nothing
            }
            break;
            case CourseType.video: {
                showView(holder.getLayoutMedia());
                holder.getTvMedia().setText("视频");
                holder.getIvMedia().setImageResource(R.drawable.breviary_ic_video);
            }
            break;
        }
    }

}
