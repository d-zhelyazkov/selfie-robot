package xrc7331.selfierobot_camera2.camera.parameter;

import android.hardware.Camera;
import xrc7331.selfierobot_camera2.camera.CameraOperationUnsupportedException;

import java.util.List;

/**
 * Created by XRC_7331 on 4/15/2016.
 */
public class CameraExposure extends CameraParameter<Integer> {

    private static final String PARAMETER_NAME = "exposure-compensation";

    public CameraExposure(Camera camera) {
        super(camera);
    }

    public void increase() throws CameraOperationUnsupportedException {
        setValue(getValue() + 1);
    }

    public void decrease() throws CameraOperationUnsupportedException {
        setValue(getValue() - 1);
    }

    @Override
    public void setValue(Integer value) throws CameraOperationUnsupportedException {
        Camera.Parameters parameters = camera.getParameters();
        int min = parameters.getMinExposureCompensation();
        int max = parameters.getMaxExposureCompensation();
        if ((value < min) || (max < value))
            throw new CameraOperationUnsupportedException();

        parameters.setExposureCompensation(value);
        camera.setParameters(parameters);

        super.notifyObservers();
    }

    @Override
    public Integer getValue() {
        Camera.Parameters parameters = camera.getParameters();
        return parameters.getExposureCompensation();
    }

    @Override
    public List<Integer> getValues() {
        return null;
    }

    @Override
    protected String getParameterName() {
        return PARAMETER_NAME;
    }
}
