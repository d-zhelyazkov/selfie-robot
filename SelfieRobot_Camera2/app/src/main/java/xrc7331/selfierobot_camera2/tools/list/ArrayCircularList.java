package xrc7331.selfierobot_camera2.tools.list;

import java.util.Collection;

/**
 * Created by XRC_7331 on 2/22/2016.
 */
public class ArrayCircularList<ElementType> implements CircularList {

    private ElementType[] array;
    private int index;

    public ArrayCircularList(ElementType... array) {
        this.array = array.clone();
    }

    public ArrayCircularList(Collection<ElementType> collection) {
        this.array = collection.toArray(array);
    }

    @Override
    public synchronized boolean next() {
        index++;
        if (index >= array.length) {
            index = 0;
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean previous() {
        index--;
        if (index < 0) {
            index = array.length - 1;
            return true;
        }
        return false;
    }

    @Override
    public ElementType getCurrent() {
        return array[index];
    }

    @Override
    public int getSize() {
        return array.length;
    }

    @Override
    public void reset() {
        index = 0;
    }
}
