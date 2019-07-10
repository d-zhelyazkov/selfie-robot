package xrc7331.selfierobot_camera2.camera.parameter;

import android.hardware.Camera;
import xrc7331.selfierobot_camera2.camera.CameraOperationUnsupportedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by XRC_7331 on 4/15/2016.
 */
public class CameraExposureLock extends CameraParameter<Boolean> {

    private static final String PARAMETER_NAME = "auto-exposure-lock";
    private static final List<Boolean> VALUES = Arrays.asList(new Boolean[]{true, false});

    public CameraExposureLock(Camera camera) {
        super(camera);
    }

    public void unlock() throws CameraOperationUnsupportedException {
        setValue(false);
    }

    public void lock() throws CameraOperationUnsupportedException {
        setValue(true);
    }

    @Override
    public void setValue(Boolean value) throws CameraOperationUnsupportedException {
        Camera.Parameters parameters = camera.getParameters();
        if (!parameters.isAutoExposureLockSupported())
            throw new CameraOperationUnsupportedException();

        parameters.setAutoExposureLock(value);
        camera.setParameters(parameters);

        super.notifyObservers();
    }

    @Override
    public Boolean getValue() {
        Camera.Parameters parameters = camera.getParameters();
        return parameters.getAutoExposureLock();
    }

    @Override
    public List<Boolean> getValues() {
        Camera.Parameters parameters = camera.getParameters();
        if (!parameters.isAutoExposureLockSupported())
            return Collections.emptyList();

        return VALUES;
    }


    @Override
    protected String getParameterName() {
        return PARAMETER_NAME;
    }

}
