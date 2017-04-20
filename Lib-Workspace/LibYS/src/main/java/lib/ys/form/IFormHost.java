package lib.ys.form;

/**
 * @author yuansui
 */
public interface IFormHost {
    <T extends FormItemEx> T getRelatedItem(Object related);

    <T extends FormItemEx> T getItem(int position);
}
