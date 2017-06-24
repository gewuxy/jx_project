package lib.ys.form;

/**
 * @author yuansui
 */
public interface FormHost {
    <T extends FormEx> T getRelatedItem(Object related);

    <T extends FormEx> T getItem(int position);
}
