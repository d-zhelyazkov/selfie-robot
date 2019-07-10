package xrc7331.selfierobot_camera2.camera;

import android.hardware.Camera;
import xrc7331.selfierobot_camera2.camera.parameter.*;
import xrc7331.selfierobot_camera2.tools.observer.Observable;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XRC_7331 on 4/10/2016.
 */
public class CustomCamera implements Observer<CameraParameter>, Observable<CustomCamera> {
    public static final int CAMERA_UPDATE_DELAY = 100;

    private final List<Observer<CustomCamera>> listeners = new LinkedList<>();
    private final Camera mCamera;

    private final CameraParameter[] parameters;
    private final CameraFocus focus;
    private final CameraISO iso;
    private final CameraExposureLock exposureLock;
    private final CameraExposure exposure;

    private boolean active = true;

    public CustomCamera(Camera mCamera) {
        this.mCamera = mCamera;

        focus = new CameraFocus(mCamera);
        iso = new CameraISO(mCamera);
        exposureLock = new CameraExposureLock(mCamera);
        exposure = new CameraExposure(mCamera);

        parameters = new CameraParameter[]{focus, iso, exposureLock, exposure};
        for (CameraParameter parameter : parameters)
            parameter.addObserver(this);
    }


    public void finishWork() {
        active = false;
        for (CameraParameter parameter : parameters)
            try {
                parameter.setToInitial();
            } catch (CameraOperationUnsupportedException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void addObserver(Observer observer) {
        listeners.add(observer);
        observer.updated(this);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : listeners)
            observer.updated(this);
    }

    @Override
    public void updated(CameraParameter caller) {
        if (active)
            this.notifyObservers();
    }

    public List<Observer<CustomCamera>> getObservers() {
        return listeners;
    }

    public CameraFocus getFocus() {
        return focus;
    }

    public CameraISO getIso() {
        return iso;
    }

    public CameraExposureLock getExposureLock() {
        return exposureLock;
    }

    public CameraExposure getExposure() {
        return exposure;
    }

    public CameraParameter[] getParameters() {
        return parameters;
    }
}
