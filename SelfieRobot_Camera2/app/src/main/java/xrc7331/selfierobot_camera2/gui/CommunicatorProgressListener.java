package xrc7331.selfierobot_camera2.gui;

import android.widget.TextView;
import xrc7331.selfierobot_camera2.MainActivity;
import xrc7331.selfierobot_camera2.R;
import xrc7331.selfierobot_camera2.robot.RobotCommunicator;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

/**
 * Created by XRC_7331 on 4/12/2016.
 */
public class CommunicatorProgressListener extends MainThreadExecutor implements Observer<RobotCommunicator> {

    private final TextView communicatorProgressView;
    private RobotCommunicator robotCommunicator;
    private final MainActivity mainActivity;
    private final Runnable executionBody = new Runnable() {
        @Override
        public void run() {
            StringBuilder builder = new StringBuilder();
            builder.append(communicatorProgressView.getText()).append("\n");
            int resId = 0;
            switch (robotCommunicator.getLastProgress()) {
                case CONNECTION_ERROR:
                    resId = R.string.conn_err;
                    break;
                case COMMUNICATION_ERROR:
                    resId = R.string.comm_err;
                    break;
                case ROBOT_REQUEST_EVALUATED:
                    resId = R.string.robot_found;
                    break;
                case ROBOT_NOT_PAIRED:
                    resId = R.string.robot_not_paired;
                    mainActivity.openBluetoothSettings();
                    break;
                case REQUEST_RECEIVED:
                    resId = R.string.robot_request;
                    break;
                case ROBOT_CONNECTED:
                    resId = R.string.robot_connected;
                    break;
                case ROBOT_REQUEST_NOT_EVALUATED:
                    resId = R.string.robot_not_found;
                    break;
            }
            builder.append(mainActivity.getString(resId));
            communicatorProgressView.setText(builder);
        }
    };

    public CommunicatorProgressListener(TextView communicatorProgressView, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.communicatorProgressView = communicatorProgressView;
    }

    @Override
    protected Runnable getExecutionBody() {
        return executionBody;
    }

    @Override
    public void updated(RobotCommunicator caller) {
        robotCommunicator = caller;
        super.executeInMainThread();
    }
}
