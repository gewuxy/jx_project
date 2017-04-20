package lib.ys.network.image.renderer;

/**
 * 模糊渲染器
 *
 * @author yuansui
 */
public class BlurRenderer extends BaseRenderer {

    private int mRadius = 15; // 默认轻度模糊, 速度快

    public BlurRenderer() {
    }

    public BlurRenderer(int radius) {
        mRadius = radius;
    }

    public int getRadius() {
        return mRadius;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BlurRenderer) {
            BlurRenderer r = (BlurRenderer) o;
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
