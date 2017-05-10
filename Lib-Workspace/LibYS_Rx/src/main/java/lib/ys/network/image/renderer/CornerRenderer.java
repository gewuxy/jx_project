package lib.ys.network.image.renderer;

/**
 * 圆角渲染器
 */
public class CornerRenderer implements Renderer {


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
