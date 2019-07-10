package xrc7331.selfierobot_camera2.robot;

import org.opencv.core.Point;
import org.opencv.core.Size;
import xrc7331.selfierobot_camera2.camera.CameraImageProcessor;
import xrc7331.selfierobot_camera2.camera.CameraOperationUnsupportedException;
import xrc7331.selfierobot_camera2.camera.CustomCamera;
import xrc7331.selfierobot_camera2.camera.parameter.CameraExposure;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by XRC_7331 on 4/6/2016.
 */
public class RobotLEDImageRecognizer implements RobotRequestEvaluator {
    private static final int RED_LEDs_COUNT = 1;
    private static final int BLUE_LEDs_COUNT = 2;
    private static final String LED_REPORT = "Red LEDs count: expected " + RED_LEDs_COUNT + ", found %d\nBlue LEDs count: expected " + BLUE_LEDs_COUNT + ", found %d";
    private static final int MAX_EXPOSURE_VALUE = 0;

    private static final RobotLEDImageRecognizer ourInstance = new RobotLEDImageRecognizer();

    public static RobotLEDImageRecognizer getInstance() {
        return ourInstance;
    }

    private final CameraImageProcessor imageProcessor = CameraImageProcessor.getInstance();

    private CustomCamera camera;

    private RobotLEDImageRecognizer() {
    }

    public void setCamera(CustomCamera camera) {
        this.camera = camera;
    }

    @Override
    public byte[] evaluate() throws RobotRequestCannotBeEvaluatedException {
        try {
            List<Point> bluePoints;
            List<Point> redPoints;
            boolean cameraAdjustmentMade = false;
            boolean increasingExposure = false;
            int exposureValue = 0;
            String report;
            CameraExposure exposure = camera.getExposure();
            do {
                if (imageProcessor.hasUnprocessedImage())
                    imageProcessor.process();

                bluePoints = imageProcessor.getExtractedContourCentroids(CameraImageProcessor.ContourType.BLUE);
                int missingBlueLEDs = BLUE_LEDs_COUNT - bluePoints.size();
                redPoints = imageProcessor.getExtractedContourCentroids(CameraImageProcessor.ContourType.RED);
                int missingRedLEDs = RED_LEDs_COUNT - redPoints.size();
                report = String.format(LED_REPORT, redPoints.size(), bluePoints.size());
                if ((missingBlueLEDs == 0) && (missingRedLEDs == 0))
                    //throw new RobotRequestCannotBeEvaluatedException(report);
                    break;

                bluePoints = null;
                redPoints = null;

                if (!cameraAdjustmentMade) {
                    cameraAdjustmentMade = true;
                    try {
                        camera.getExposureLock().unlock();
                        exposureValue = exposure.getValue();
                    } catch (CameraOperationUnsupportedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!increasingExposure) {
                        try {
                            exposure.decrease();
                        } catch (CameraOperationUnsupportedException e) {
                            increasingExposure = true;
                            try {
                                exposure.setValue(exposureValue);
                            } catch (CameraOperationUnsupportedException e1) {
                                e1.printStackTrace();
                                break;
                            }
                        }
                    } else {
                        if (exposure.getValue() == MAX_EXPOSURE_VALUE)
                            break;

                        try {
                            exposure.increase();
                        } catch (CameraOperationUnsupportedException e) {
                            break;
                        }
                    }
                }

                Thread.sleep(CustomCamera.CAMERA_UPDATE_DELAY);

            } while (true);
            if (bluePoints == null) {
                try {
                    exposure.setValue(exposureValue);
                } catch (CameraOperationUnsupportedException e) {
                    e.printStackTrace();
                }
                throw new RobotRequestCannotBeEvaluatedException(report);
            }

            try {
                camera.getExposureLock().lock();
            } catch (CameraOperationUnsupportedException e) {
                e.printStackTrace();
            }


            Size imageSize = imageProcessor.getImage(CameraImageProcessor.ImageType.BLUE_SAT_BIN).size();
            Point centerPoint = new Point(imageSize.width /= 2, imageSize.height /= 2);
            Point[] pointsArray = {redPoints.get(0), bluePoints.get(0), bluePoints.get(1)};
            ByteBuffer resultBuffer = ByteBuffer.allocate(3 * 4 * 2);
            for (Point point : pointsArray) {
                Point realPoint = convertToRealPoint(point, centerPoint);
                int x = (int) realPoint.x;
                int y = (int) realPoint.y;
                resultBuffer.putInt(x);
                resultBuffer.putInt(y);
            }

            return resultBuffer.array();

        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RobotRequestCannotBeEvaluatedException();
        }

    }

    private Point convertToRealPoint(Point point, Point center) {
        Point realPoint = new Point();
        realPoint.x = point.x - center.x;
        realPoint.y = center.y - point.y;
        return realPoint;
    }
}
