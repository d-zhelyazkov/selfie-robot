package xrc7331.selfierobot_camera2.tools.list;

import java.util.ListIterator;

/**
 * Created by XRC_7331 on 2/22/2016.
 */
public interface CircularIterator<ElementType> extends ListIterator<ElementType> {
    ElementType getCurrent();
    int getSize();

    ElementType first();
}
