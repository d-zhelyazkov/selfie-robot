package xrc7331.selfierobot_camera2.camera.parameter;

import android.hardware.Camera;
import xrc7331.selfierobot_camera2.camera.CameraOperationUnsupportedException;

import java.util.List;

/**
 * Created by XRC_7331 on 4/15/2016.
 */
public class CameraFocus extends CameraParameter<String> {

    private static final String PARAMETER_NAME = "focus-mode";
    private static final String[] MANUAL_FOCUS_MODES = {"manual", "fixed"};

    public CameraFocus(Camera camera) {
        super(camera);
    }


    public void setToManual() throws CameraOperationUnsupportedException {
        for(String focusMode : MANUAL_FOCUS_MODES){
            try {
                setValue(focusMode);
                return;
            } catch (CameraOperationUnsupportedException e) {
                e.printStackTrace();
            }
        }

        throw new CameraOperationUnsupportedException();
    }

    @Override
    public void setValue(String value) throws CameraOperationUnsupportedException {
        if (!getValues().contains(value))
            throw new CameraOperationUnsupportedException();

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(value);
        camera.setParameters(parameters);

        super.notifyObservers();
    }

    @Override
    public String getValue() {
        Camera.Parameters parameters = camera.getParameters();
        return parameters.getFocusMode();
    }

    @Override
    public List<String> getValues() {
        Camera.Parameters parameters = camera.getParameters();
        return parameters.getSupportedFocusModes();
    }

    @Override
    protected String getParameterName() {
        return PARAMETER_NAME;
    }


}
