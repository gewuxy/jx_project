package yy.doctor.activity;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lib.ys.LogMgr;
import lib.yy.activity.base.BaseActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import yy.doctor.model.Profile;

/**
 * @auther yuansui
 * @since 2017/5/6
 */

abstract public class RxActivityEx extends BaseActivity {

    private Retrofit mRetrofit;

    public interface UserAPI {
        @GET("login")
        Observable<Profile> login(@Query("username") String name,
                                  @Query("password") String pwd);
    }

    protected <T> T createRequest(Class<T> service) {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(new EValFactory())
                    .baseUrl("http://www.medcn.com:8081/v7/api/")
                    .build();
        }

        return mRetrofit.create(service);
    }


    protected <T> void exe(Observable<T> call) {
        call
//                .doAfterNext(t -> LogMgr.d("www", "exe: doAfterNext = " + t))
                .onTerminateDetach()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<T>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onNext(T t) {
                        onNetworkSuccess(0, t);
                        LogMgr.d("www", "onNext: t = " + t);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogMgr.e(TAG, "onError", throwable);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    protected void test(String name, String pwd) {


//        retrofit.create(UserAPI.class)
//                .login(name, pwd)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.computation())
//                .subscribe(new Observer<Profile>() {
//
//                    @Override
//                    public void onSubscribe(Disposable disposable) {
//                    }
//
//                    @Override
//                    public void onNext(Profile profile) {
//                        LogMgr.d(TAG, "onNext: pro = " + profile);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        LogMgr.e(TAG, "onError()", throwable);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });
    }
}
