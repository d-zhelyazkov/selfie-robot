package xrc7331.selfierobot_camera2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import xrc7331.selfierobot_camera2.bluetooth.BluetoothModule;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTAdapterNotAvailableException;
import xrc7331.selfierobot_camera2.bluetooth.exception.BTNotEnabledException;
import xrc7331.selfierobot_camera2.camera.CameraImageProcessor;
import xrc7331.selfierobot_camera2.camera.CustomCamera;
import xrc7331.selfierobot_camera2.camera.CustomCameraView;
import xrc7331.selfierobot_camera2.gui.*;
import xrc7331.selfierobot_camera2.robot.RobotCommunicator;

public class MainActivity extends Activity {

    private CustomCameraView customCameraView;
    private CameraImageProcessor imageProcessor = CameraImageProcessor.getInstance();
    private BluetoothModule btModule = new BluetoothModule();
    private RobotCommunicator robotCommunicator;
    private DrawBoard drawBoard;
    private TextView imageTypeView;
    private TextView communicatorProgressView;
    private TextView cameraParametersView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
//                    Log.i(TAG, "OpenCV loaded successfully");
                    customCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    private AsyncTask processingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageTypeView = (TextView) findViewById(R.id.image_type);
        communicatorProgressView = (TextView) findViewById(R.id.communicator_progress);
        cameraParametersView = (TextView) findViewById(R.id.camera_parameters);

        customCameraView = (CustomCameraView) findViewById(R.id.camera_view);
        customCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        customCameraView.addObserver(new MonitorTypeChangeListener(imageTypeView, this.getResources()));
        customCameraView.addCameraObserver(new CameraParametersListener(cameraParametersView));

        drawBoard = (DrawBoard) findViewById(R.id.info_view);
        imageProcessor.addObserver(new ProcessedImageListener(drawBoard));

        ToggleButton processSwitch = (ToggleButton) findViewById(R.id.process_switch);
        processSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startProcessingThread();
                } else {
                    stopProcessingThread();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        try {
            btModule.init();
        } catch (BTAdapterNotAvailableException e) {
            e.printStackTrace();
        } catch (BTNotEnabledException e) {
            e.printStackTrace();
            openBluetoothSettings();
            return;
        }
        startCommunicatingThread();
        //startProcessingThread();
    }

    private void startCommunicatingThread() {
        if (robotCommunicator != null)
            return;

        robotCommunicator = new RobotCommunicator(btModule);
        robotCommunicator.addObserver(new CommunicatorProgressListener(communicatorProgressView, this));
        robotCommunicator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void openBluetoothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    private void startProcessingThread() {
        if (processingThread != null)
            return;

        processingThread = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                while (!this.isCancelled()) {
                    try {
                        imageProcessor.process();
                        super.publishProgress();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

        };
        processingThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void stopProcessingThread() {
        if (processingThread != null) {
            processingThread.cancel(true);
            processingThread = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finnishWork();
    }

    public void onDestroy() {
        super.onDestroy();
        finnishWork();
    }

    private void finnishWork() {
        stopProcessingThread();

        btModule.finishWork();
        if (robotCommunicator != null) {
            robotCommunicator.cancel(true);
            robotCommunicator = null;
        }

        if (customCameraView != null) {
            CustomCamera camera = customCameraView.getCamera();
            if (camera != null)
                camera.finishWork();
            customCameraView.disableView();
        }

    }

}
