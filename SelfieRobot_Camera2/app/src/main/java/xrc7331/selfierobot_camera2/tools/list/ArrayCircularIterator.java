package xrc7331.selfierobot_camera2.tools.list;

import java.util.List;

/**
 * Created by XRC_7331 on 2/22/2016.
 */
public class ArrayCircularIterator<ElementType> implements CircularIterator<ElementType> {

    private ElementType[] array;
    private int index;

    public ArrayCircularIterator(ElementType... array) {
        this.array = array.clone();
    }

    public ArrayCircularIterator(List<ElementType> collection) {
        array = (ElementType[]) collection.toArray();
    }

    @Override
    public void add(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public ElementType next() {
        index = nextIndex();
        return getCurrent();
    }

    @Override
    public int nextIndex() {
        return validateIX(this.index + 1);
    }

    @Override
    public ElementType previous() {
        index = previousIndex();
        return getCurrent();
    }

    @Override
    public int previousIndex() {
        return validateIX(index - 1);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object object) {
        throw new UnsupportedOperationException();
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
    public ElementType first() {
        index = 0;
        return getCurrent();
    }

    private int validateIX(int ix) {
        ix += array.length;
        return ix % array.length;
    }
}
