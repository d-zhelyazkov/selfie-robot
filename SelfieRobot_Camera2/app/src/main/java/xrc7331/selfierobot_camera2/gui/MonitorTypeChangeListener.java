package xrc7331.selfierobot_camera2.gui;

import android.content.res.Resources;
import android.widget.TextView;
import xrc7331.selfierobot_camera2.camera.CameraImageProcessor;
import xrc7331.selfierobot_camera2.camera.CustomCameraView;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

/**
 * Created by XRC_7331 on 4/12/2016.
 */
public class MonitorTypeChangeListener extends MainThreadExecutor implements Observer<CustomCameraView> {

    private CustomCameraView cameraView;
    private final TextView imageTypeText;
    private final Resources resources;
    private final Runnable executionBody = new Runnable() {
        @Override
        public void run() {
            CameraImageProcessor.ImageType imageType = cameraView.getMonitorTypes().getCurrent();
            String text = imageType.toString();
//            switch (imageType) {
//
//                case INPUT:
//                    text = resources.getString(R.string.camera);
//                    break;
//                case BLUE:
//                    text = resources.getString(R.string.blue_chnl);
//                    break;
//                case RED:
//                    text = resources.getString(R.string.red_chnl);
//                    break;
//                case SAT:
//                    text = resources.getString(R.string.sat_chnl);
//                    break;
//                case VALUE:
//                    text = resources.getString(R.string.val_chnl);
//                    break;
//                case BLUE_SAT:
//                    text = resources.getString(R.string.blue_sat);
//                    break;
//                case BLUE_SAT_BIN:
//                    text = resources.getString(R.string.blue_bin);
//                    break;
//                case RED_SAT:
//                    text = resources.getString(R.string.red_sat);
//                    break;
//                case RED_SAT_BIN:
//                    text = resources.getString(R.string.red_bin);
//                    break;
//                case RESULT:
//                    break;
//            }
            imageTypeText.setText(text);
        }
    };

    public MonitorTypeChangeListener(TextView textView, Resources resources) {
        this.imageTypeText = textView;
        this.resources = resources;
    }

    protected Runnable getExecutionBody() {
        return executionBody;
    }

    @Override
    public void updated(CustomCameraView caller) {
        cameraView = caller;
        super.executeInMainThread();
    }
}
