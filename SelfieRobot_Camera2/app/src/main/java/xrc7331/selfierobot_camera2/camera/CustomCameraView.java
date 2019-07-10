package xrc7331.selfierobot_camera2.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import xrc7331.selfierobot_camera2.opencv.ExMat;
import xrc7331.selfierobot_camera2.robot.RobotLEDImageRecognizer;
import xrc7331.selfierobot_camera2.tools.list.ArrayCircularIterator;
import xrc7331.selfierobot_camera2.tools.list.CircularIterator;
import xrc7331.selfierobot_camera2.tools.observer.Observable;
import xrc7331.selfierobot_camera2.tools.observer.Observer;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by XRC_7331 on 3/30/2016.
 */
public class CustomCameraView extends JavaCameraView implements Observable<CustomCameraView> {

    private final CustomCameraView customCameraView = this;
    private CustomCamera camera;
    private final CircularIterator<CameraImageProcessor.ImageType> monitorTypes = new ArrayCircularIterator<>(
            CameraImageProcessor.ImageType.INPUT,
            CameraImageProcessor.ImageType.BLURRED,
            CameraImageProcessor.ImageType.SAT,
            CameraImageProcessor.ImageType.VALUE,
            CameraImageProcessor.ImageType.BLUE,
            CameraImageProcessor.ImageType.BLUE_SAT,
            CameraImageProcessor.ImageType.BLUE_SAT_BIN,
            CameraImageProcessor.ImageType.RED,
            CameraImageProcessor.ImageType.RED_SAT,
            CameraImageProcessor.ImageType.RED_SAT_BIN);
    private final CameraImageProcessor imageProcessor = CameraImageProcessor.getInstance();
    private CustomClickListener clickListener = new CustomClickListener();

    private List<Observer<CustomCameraView>> listeners = new LinkedList<>();
    private Set<Observer<CustomCamera>> cameraListeners = new LinkedHashSet<>();

    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);


        super.setCvCameraViewListener(new CustomCameraViewListener());
        super.setOnClickListener(clickListener);
    }

    public CircularIterator<CameraImageProcessor.ImageType> getMonitorTypes() {
        return monitorTypes;
    }

    public void addCameraObserver(Observer<CustomCamera> observer) {
        cameraListeners.add(observer);
    }

    @Override
    public void addObserver(Observer<CustomCameraView> observer) {
        listeners.add(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer listener : listeners)
            listener.updated(this);
    }

    class CustomCameraViewListener implements CameraBridgeViewBase.CvCameraViewListener2 {
        @Override
        public void onCameraViewStarted(int width, int height) {
            //customCameraView.setDesiredCameraParameters();
            monitorTypes.first();
            clickListener.firstClick = true;
            camera = new CustomCamera(mCamera);
            for (Observer listener : cameraListeners)
                camera.addObserver(listener);
            try {
                camera.getIso().setMinISO();
                camera.getExposureLock().lock();
            } catch (CameraOperationUnsupportedException e) {
                e.printStackTrace();
            }

            RobotLEDImageRecognizer.getInstance().setCamera(camera);
            customCameraView.notifyObservers();
        }

        @Override
        public void onCameraViewStopped() {
            cameraListeners.addAll(camera.getObservers());
            camera = null;
        }


        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            Mat image = inputFrame.rgba();

            try {
                if (imageProcessor != null) {
                    ExMat exRgba = new ExMat(ExMat.ColorType.RGBA, image);
                    imageProcessor.setInputImage(exRgba);
                    exRgba.release();

                    CameraImageProcessor.ImageType imageType = monitorTypes.getCurrent();
                    if (imageType != CameraImageProcessor.ImageType.INPUT) {
                        ExMat outputImage = imageProcessor.getImage(imageType);
                        if (outputImage != null) {
                            image.release();
                            return outputImage;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }

            return image;
        }


    }

    public CustomCamera getCamera() {
        return camera;
    }

    class CustomClickListener implements View.OnClickListener {

        private boolean firstClick = true;

        @Override
        public void onClick(View v) {
            if (firstClick) {
                firstClick = false;
                try {
                    camera.getFocus().setToManual();
                } catch (CameraOperationUnsupportedException e) {
                    e.printStackTrace();
                }

            } else {
                monitorTypes.next();
                customCameraView.notifyObservers();
            }

        }
    }

}


