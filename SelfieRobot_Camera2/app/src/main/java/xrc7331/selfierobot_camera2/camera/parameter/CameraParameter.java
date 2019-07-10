package xrc7331.selfierobot_camera2.camera.parameter;

import android.hardware.Camera;
import xrc7331.selfierobot_camera2.camera.CameraOperationUnsupportedException;
import xrc7331.selfierobot_camera2.tools.observer.Observable;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XRC_7331 on 4/15/2016.
 */
public abstract class CameraParameter<ValueType> implements Observable<CameraParameter> {
    private static final String TO_STRING_FORMAT = "%s: %s";


    private List<Observer<CameraParameter>> listeners = new LinkedList<>();

    protected Camera camera;
    private ValueType initialValue;


    public CameraParameter(Camera camera) {
        this.camera = camera;

        initialValue = getValue();
    }

    public abstract void setValue(ValueType value) throws CameraOperationUnsupportedException;

    public abstract ValueType getValue();

    public abstract List<ValueType> getValues();


    public void setToInitial() throws CameraOperationUnsupportedException {
        setValue(initialValue);
    }

    protected abstract String getParameterName();


    @Override
    public void addObserver(Observer<CameraParameter> observer) {
        listeners.add(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer listener : listeners)
            listener.updated(this);
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT, getParameterName(), getValue());
    }

}
