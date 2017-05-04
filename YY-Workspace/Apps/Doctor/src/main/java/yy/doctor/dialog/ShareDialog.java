package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/19
 */
public class ShareDialog extends BaseDialog {

    private ImageView mIvWechatFriendCircle;
    private ImageView mIvWechatFriends;
    private ImageView mIvSina;

    private TextView mTvCancel;

    public ShareDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_share;
    }

    @Override
    public void findViews() {

        mIvWechatFriendCircle = findView(R.id.dialog_share_iv_wechat_friend_cicle);
        mIvWechatFriends = findView(R.id.dialog_share_iv_wechat_friends);
        mIvSina = findView(R.id.dialog_share_iv_sina);

        mTvCancel = findView(R.id.dialog_share_tv_cancel);

    }

    @Override
    public void setViews() {

        mIvWechatFriendCircle.setOnClickListener(this);
        mIvWechatFriends.setOnClickListener(this);
        mIvSina.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);

        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.dialog_share_iv_wechat_friend_cicle: {
                showToast("777");
            }
            break;
            case R.id.dialog_share_iv_wechat_friends: {
                showToast("852");
            }
            break;
            case R.id.dialog_share_iv_sina: {
                showToast("745");
            }
            break;
            case R.id.dialog_share_tv_cancel: {
                showToast("66");
            }
            break;
        }

    }
}
