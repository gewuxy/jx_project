package lib.ys.network.image.renderer;

/**
 * 圆角渲染器
 */
public class CornerRenderer extends BaseRenderer {

    /**
     * 以下是网络对于glide方式的定义, 暂时保留
     */
//    @IntDef({
//            CornerType.all,
//            CornerType.top_left,
//            CornerType.top_right,
//            CornerType.bottom_left,
//            CornerType.bottom_right,
//            CornerType.top,
//            CornerType.bottom,
//            CornerType.left,
//            CornerType.right,
//            CornerType.other_top_left,
//            CornerType.other_top_right,
//            CornerType.other_bottom_left,
//            CornerType.other_bottom_right,
//            CornerType.diagonal_from_top_left,
//            CornerType.diagonal_from_top_right,
//    })
//    public @interface CornerType {
//        int all = 0;
//        int top_left = 1;
//        int top_right = 2;
//        int bottom_left = 3;
//        int bottom_right = 4;
//        int top = 5;
//        int bottom = 6;
//        int left = 7;
//        int right = 8;
//        int other_top_left = 9;
//        int other_top_right = 10;
//        int other_bottom_left = 11;
//        int other_bottom_right = 12;
//        int diagonal_from_top_left = 13;
//        int diagonal_from_top_right = 14;
//    }

    private int mRadius = 10;

    public CornerRenderer() {
    }

    public CornerRenderer(int radius) {
        mRadius = radius;
    }

    public int getRadius() {
        return mRadius;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CornerRenderer) {
            CornerRenderer r = (CornerRenderer) o;
            if (r.mRadius == this.mRadius) {
                return true;
            } else {
                return false;
            }
        } else {
            return super.equals(o);
        }
    }
}
