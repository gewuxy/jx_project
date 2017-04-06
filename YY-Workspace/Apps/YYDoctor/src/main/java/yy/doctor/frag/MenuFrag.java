package yy.doctor.frag;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.frag.base.BaseFormFrag;
import yy.doctor.R;
import yy.doctor.model.form.menu.Builder;
import yy.doctor.model.form.menu.MenuType;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class MenuFrag extends BaseFormFrag {

    @IntDef({
            RelatedId.msg,
            RelatedId.post,
    })
    private @interface RelatedId {
        int msg = 0;
        int post = 1;
    }


    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(MenuType.divider)
                .height(fitDp(100))
                .background(Color.TRANSPARENT)
                .build());

        addItem(new Builder(MenuType.divider).build());

        addItem(new Builder(MenuType.child)
                .name("消息")
                .drawable(R.mipmap.menu_ic_msg)
                .related(RelatedId.msg)
                .build());

        addItem(new Builder(MenuType.child)
                .name("我的帖子")
                .drawable(R.mipmap.menu_ic_my_post)
                .build());

        addItem(new Builder(MenuType.child)
                .name("会议提醒")
                .drawable(R.mipmap.menu_ic_meeting_remind)
                .build());

        addItem(new Builder(MenuType.child)
                .name("邀请好友")
                .drawable(R.mipmap.menu_ic_invite_friend)
                .build());

        addItem(new Builder(MenuType.divider).build());

        addItem(new Builder(MenuType.group)
                .name("文件夹")
                .build());

        addItem(new Builder(MenuType.divider).build());

        addItem(new Builder(MenuType.child)
                .name("我的文件")
                .drawable(R.mipmap.menu_ic_my_file)
                .build());

        addItem(new Builder(MenuType.child)
                .name("我的图片")
                .drawable(R.mipmap.menu_ic_my_img)
                .build());
    }

    @Override
    public void initTitleBar() {
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.msg: {
                showToast("点击了消息");
            }
            break;
        }


    }
}
