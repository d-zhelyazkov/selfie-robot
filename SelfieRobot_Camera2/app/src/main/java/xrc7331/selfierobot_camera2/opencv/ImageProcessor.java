package xrc7331.selfierobot_camera2.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import xrc7331.selfierobot_camera2.tools.UnsignedByte;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by XRC_7331 on 3/9/2016.
 */
public class ImageProcessor {
    private static ImageProcessor INSTANCE = new ImageProcessor();

    public static ImageProcessor getInstance() {
        return INSTANCE;
    }


    private ImageProcessor() {
    }

    public List<ExMat> splitChannels(ExMat image) {
        List<Mat> splitChannels = new LinkedList<>();
        Core.split(image, splitChannels);

        List<ExMat> result = new LinkedList<>();
        for (Mat mat : splitChannels) {
            result.add(new ExMat(ExMat.ColorType.GRAY, mat));
            mat.release();
        }

        return result;
    }

    public Point calculateCentroid(MatOfPoint contour) {
        Moments moments = Imgproc.moments(contour);
        Point centroid = new Point(0, 0);
        centroid.x = (moments.get_m10() / moments.get_m00());
        centroid.y = (moments.get_m01() / moments.get_m00());

        //assert (!Double.isNaN(centroid.x)) : "NaN";
        if ((Double.isNaN(centroid.x)) || (Double.isNaN(centroid.y)) || (centroid.x == 0 && centroid.y == 0))
            centroid = calculateCentroid(contour.toList());

        assert (!Double.isNaN(centroid.x)) : "NaN";
        return centroid;
    }

    public Point calculateCentroid(Collection<Point> points) {
        Point centroid = new Point(0, 0);
        for (Point point : points) {
            centroid.x += point.x;
            centroid.y += point.y;
        }
        centroid.x /= points.size();
        centroid.y /= points.size();

        return centroid;
    }

    public ExMat merge(ExMat img1, ExMat img2) {
        byte[] data1 = img1.getData();
        byte[] data2 = img2.getData();

        byte[] resultData = new byte[data1.length];
        for (int i = 0; i < data1.length; i++) {
            int int1 = UnsignedByte.toInt(data1[i]);
            int int2 = UnsignedByte.toInt(data2[i]);
            //int diff = Math.abs(int1 - int2);
            //resultData[i] = (byte) (unsignedByteTool.getMaxInt() - diff);
            resultData[i] = (byte) ((int1 + int2) >> 1);
//            resultData[i] = (byte) ((int1 < int2) ? int1 : int2);
            //resultData[i] = operator.operate(data1[i], data2[i]);
        }

        ExMat result = new ExMat(img1);
        return result;

    }

    public ExMat and(ExMat img1, ExMat img2) {
        ExMat result = new ExMat(img1.getColorType());
        Core.bitwise_and(img1, img2, result);
        return result;
    }

    public ExMat or(ExMat img1, ExMat img2) {
        ExMat result = new ExMat(img1.getColorType());
        Core.bitwise_or(img1, img2, result);
        return result;
    }

    public Collection<MatOfPoint> findContours(ExMat image) {
        List<MatOfPoint> contours = new LinkedList<>();
        Mat buffer = image.clone();
        Imgproc.findContours(buffer, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
        buffer.release();
        return contours;
    }

    public List<Point> extractContourCentroids(ExMat binaryImage) {
        Collection<MatOfPoint> contours = this.findContours(binaryImage);
        List<Point> contourCentroids = new LinkedList<>();
        for (MatOfPoint contour : contours) {
            contourCentroids.add(this.calculateCentroid(contour));
        }

        return contourCentroids;
    }

}

