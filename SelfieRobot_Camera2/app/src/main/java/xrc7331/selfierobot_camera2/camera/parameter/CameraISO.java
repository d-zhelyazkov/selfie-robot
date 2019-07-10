package xrc7331.selfierobot_camera2.camera.parameter;

import android.hardware.Camera;
import xrc7331.selfierobot_camera2.camera.CameraOperationUnsupportedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by XRC_7331 on 4/15/2016.
 */
public class CameraISO extends CameraParameter<String> {

    private static final String PARAMETER_NAME = "iso";
    private static final String VALUES_SUFFIX = "-values";

    public CameraISO(Camera camera) {
        super(camera);
    }

    public void setMinISO() throws CameraOperationUnsupportedException {
        List<String> values = getValues();
        for (String value : values) {
            try {
                Short.parseShort(value);
                setValue(value);
                break;
            } catch (NumberFormatException e) {
            }
        }

        throw new CameraOperationUnsupportedException();
    }

    @Override
    public void setValue(String value) throws CameraOperationUnsupportedException {
        if (!getValues().contains(value))
            throw new CameraOperationUnsupportedException();

        Camera.Parameters parameters = camera.getParameters();
        parameters.set(PARAMETER_NAME, value);
        camera.setParameters(parameters);

        super.notifyObservers();
    }

    @Override
    public String getValue() {
        Camera.Parameters parameters = camera.getParameters();
        return parameters.get(PARAMETER_NAME);
    }

    @Override
    public List<String> getValues() {
        Camera.Parameters parameters = camera.getParameters();
        String valuesStr = parameters.get(PARAMETER_NAME + VALUES_SUFFIX);
        if (valuesStr == null)
            return Collections.emptyList();

        return Arrays.asList(valuesStr.split(","));
    }

    @Override
    protected String getParameterName() {
        return PARAMETER_NAME;
    }
}
