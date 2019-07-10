package xrc7331.selfierobot_camera2;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import xrc7331.selfierobot_camera2.opencv.ColorType;
import xrc7331.selfierobot_camera2.opencv.ExMat;
import xrc7331.selfierobot_camera2.tools.list.ArrayCircularList;

/**
 * Created by XRC_7331 on 3/30/2016.
 */
public class CustomCameraView extends JavaCameraView {

    private static final Point TEXT_POSITION = new Point(20, 50);
    private static final Scalar TEXT_COLOR = new Scalar(255, 255, 255);
    private static final double TEXT_FONT_SCALE = 5;
    private static final ArrayCircularList<MyImageProcessor.ImageType> MONITOR_TYPES = new ArrayCircularList<>(
            MyImageProcessor.ImageType.INPUT,
            MyImageProcessor.ImageType.SAT,
            MyImageProcessor.ImageType.VALUE,
            MyImageProcessor.ImageType.BLUE,
            MyImageProcessor.ImageType.BLUE_SAT,
            MyImageProcessor.ImageType.BLUE_SAT_BIN,
            MyImageProcessor.ImageType.RED,
            MyImageProcessor.ImageType.RED_SAT,
            MyImageProcessor.ImageType.RED_SAT_BIN,
            MyImageProcessor.ImageType.RESULT);

    private MyImageProcessor imageProcessor;
    private AsyncTask workThread;
    CustomClickListener clickListener = new CustomClickListener();

    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);


        super.setCvCameraViewListener(new CustomCameraViewListener());
        super.setOnClickListener(clickListener);
    }

    public void setImageProcessor(MyImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    public void setDesiredCameraParameters() {
        //mCamera.cancelAutoFocus();
        mCamera.cancelAutoFocus();
        Camera.Parameters parameters = mCamera.getParameters();
        //parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        //finds and sets min ISO
        String isoValuesStr = parameters.get("iso-values");
        if (isoValuesStr != null) {
            String[] isoValues = isoValuesStr.split(",");
            short minIso = Short.MAX_VALUE;
            for (String isoValue : isoValues) {
                try {
                    short iso = Short.parseShort(isoValue);
                    if (minIso > iso)
                        minIso = iso;
                } catch (NumberFormatException e) {
                }
            }
            parameters.set("iso", Short.toString(minIso));
        }

        //disables auto-focus
        String focusModeValuesStr = parameters.get("focus-mode-values");
        if (focusModeValuesStr != null) {
            String[] suitableModes = {"manual", "fixed"};
            for (String mode : suitableModes) {
                if (focusModeValuesStr.contains(mode)) {
                    parameters.setFocusMode(mode);
                    break;
                }
            }
        }
        parameters.set("shutter-speed", "15");
        mCamera.setParameters(parameters);
    }

    private void cancelProcessingThread() {
        if (workThread != null) {
            workThread.cancel(false);
            workThread = null;
        }
    }

    private void startProcessingThread() {
        if (workThread == null && imageProcessor != null) {
            workThread = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    while (!this.isCancelled()) {
                        try {
                            imageProcessor.process();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            };
            workThread.execute();
        }
    }


    class CustomCameraViewListener implements CameraBridgeViewBase.CvCameraViewListener2 {
        @Override
        public void onCameraViewStarted(int width, int height) {
            //customCameraView.setDesiredCameraParameters();
            MONITOR_TYPES.reset();
            clickListener.firstClick = true;
        }

        @Override
        public void onCameraViewStopped() {
            cancelProcessingThread();
        }


        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            final Mat image = inputFrame.rgba();

            try {
                if (imageProcessor != null) {
                    ExMat exRgba = new ExMat(ExMat.ColorType.RGBA, image);
                    imageProcessor.setInputImage(exRgba);
                    exRgba.release();

                    MyImageProcessor.ImageType imageType = MONITOR_TYPES.getCurrent();
                    if (imageType == MyImageProcessor.ImageType.INPUT) {
                        cancelProcessingThread();
                    } else {
                        startProcessingThread();
                        ExMat outputImage = imageProcessor.getImage(imageType);
                        if (outputImage != null) {
                            Imgproc.putText(outputImage, imageType.name, TEXT_POSITION, Core.FONT_HERSHEY_PLAIN, TEXT_FONT_SCALE, TEXT_COLOR);

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

    class CustomClickListener implements View.OnClickListener {

        private boolean firstClick = true;

        @Override
        public void onClick(View v) {
            if (firstClick) {
                firstClick = false;
                setDesiredCameraParameters();
            } else {
                MONITOR_TYPES.next();
            }
        }
    }
}


