package xrc7331.selfierobot_camera2.tools.list;

/**
 * Created by XRC_7331 on 2/22/2016.
 */
public interface CircularList<ElementType> {
    boolean next();
    boolean previous();
    ElementType getCurrent();
    int getSize();

    void reset();
}
