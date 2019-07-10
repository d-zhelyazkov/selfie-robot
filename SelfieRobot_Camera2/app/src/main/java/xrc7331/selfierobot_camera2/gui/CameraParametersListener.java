package xrc7331.selfierobot_camera2.gui;

import android.widget.TextView;
import xrc7331.selfierobot_camera2.camera.CustomCamera;
import xrc7331.selfierobot_camera2.camera.parameter.CameraParameter;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

/**
 * Created by XRC_7331 on 4/12/2016.
 */
public class CameraParametersListener extends MainThreadExecutor implements Observer<CustomCamera> {

    private TextView cameraPropertiesView;
    private CustomCamera customCamera;
    private final Runnable executionBody = new Runnable() {
        @Override
        public void run() {
            StringBuilder stringBuilder = new StringBuilder();
            boolean first = true;
            for (CameraParameter parameter : customCamera.getParameters()) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(parameter);
            }
            cameraPropertiesView.setText(stringBuilder.toString());
        }
    };

    public CameraParametersListener(TextView cameraPropertiesView) {
        this.cameraPropertiesView = cameraPropertiesView;
    }

    @Override
    protected Runnable getExecutionBody() {
        return executionBody;
    }

    @Override
    public void updated(CustomCamera caller) {
        customCamera = caller;
        super.executeInMainThread();
    }
}
