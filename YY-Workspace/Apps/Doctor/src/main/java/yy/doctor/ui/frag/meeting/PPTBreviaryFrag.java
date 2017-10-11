package yy.doctor.ui.frag.meeting;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseVPFrag;
import yy.doctor.R;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.ui.frag.meeting.course.BreviaryFrag;
import yy.doctor.ui.frag.meeting.course.BreviaryFragRouter;

/**
 * 缩略图部分(暂时弃用)
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */

public class PPTBreviaryFrag extends BaseVPFrag implements OnPageChangeListener {

    private final int KVpSize = 4; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长

    private float mLastOffset;

    public void setCourses(List<Course> courses) {
        // 逐个添加Frag
        for (Course course : courses) {
            @CourseType int type = course.getType();
            BreviaryFrag f = BreviaryFragRouter.create(type, course.getString(TCourse.imgUrl)).route();
            if (f != null) {
                add(f);
            }
        }
        invalidate();
    }

    @Override
    public void initData() {
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_ppt_breviary;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);
        setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition;
        float realOffset;
        int nextPosition;

        if (mLastOffset > positionOffset) {
            realPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            realPosition = position;
            nextPosition = position + 1;
            realOffset = positionOffset;
        }

        if (nextPosition > getCount() - 1 || realPosition > getCount() - 1) {
            return;
        }

        viewChange(realPosition, 1 - realOffset);
        viewChange(nextPosition, realOffset);

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
        // do nothing
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }

    /**
     * 改变view的大小
     */
    private void viewChange(int position, float offset) {
        View view = getItem(position).getView();
        if (view == null) {
            return;
        }
        float scale = 1 + 0.15f * offset;
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

}
