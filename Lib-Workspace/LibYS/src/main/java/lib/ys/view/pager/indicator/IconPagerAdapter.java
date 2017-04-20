package lib.ys.view.pager.indicator;

public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIndicatorResId(int index);

    // From PagerAdapter
    int getCount();

    /**
     * 标识是否为循环，若要做无线循环，该实现方法要返回true
     *
     * @return
     */
    boolean isLoop();

    /**
     * 该方法返回真实的个数
     *
     * @return
     */
    int getRealCount();
}
