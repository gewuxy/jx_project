package yy.doctor.frag;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;

import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.ui.other.NavBar;
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
            RelatedId.remind,
            RelatedId.friend,
            RelatedId.file,
            RelatedId.img,
    })
    private @interface RelatedId {
        int msg = 0;
        int post = 1;
        int remind = 2;
        int friend = 3;
        int file = 4;
        int img = 5;
    }


    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(MenuType.divider)
                .height(fitDp(120))
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
                .related(RelatedId.post)
                .build());

        addItem(new Builder(MenuType.child)
                .name("会议提醒")
                .drawable(R.mipmap.menu_ic_meeting_remind)
                .related(RelatedId.remind)
                .build());

        addItem(new Builder(MenuType.child)
                .name("邀请好友")
                .drawable(R.mipmap.menu_ic_invite_friend)
                .related(RelatedId.friend)
                .build());

        addItem(new Builder(MenuType.divider).build());

        addItem(new Builder(MenuType.group)
                .name("文件夹")
                .build());

        addItem(new Builder(MenuType.divider).build());

        addItem(new Builder(MenuType.child)
                .name("我的文件")
                .drawable(R.mipmap.menu_ic_my_file)
                .related(RelatedId.file)
                .build());

        addItem(new Builder(MenuType.child)
                .name("我的图片")
                .drawable(R.mipmap.menu_ic_my_img)
                .related(RelatedId.img)
                .build());
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.msg: {
                showToast("点击了消息");
            }
            break;
            case RelatedId.post: {
                showToast("点击了帖子");
            }
            break;
            case RelatedId.remind: {
                showToast("点击了提醒");
            }
            break;
            case RelatedId.friend: {
                showToast("点击了好友");
            }
            break;
            case RelatedId.file: {
                showToast("点击了文件");
            }
            break;
            case RelatedId.img: {
                showToast("点击了图片");
            }
            break;
        }


    }
}
