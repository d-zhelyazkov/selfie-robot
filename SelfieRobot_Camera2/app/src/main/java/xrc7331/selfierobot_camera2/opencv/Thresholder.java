package xrc7331.selfierobot_camera2.opencv;

import org.opencv.imgproc.Imgproc;
import xrc7331.selfierobot_camera2.tools.HistogramCreator;
import xrc7331.selfierobot_camera2.tools.UnsignedByte;
import xrc7331.selfierobot_camera2.tools.threshold_finder.ThresholdFinder;

/**
 * Created by XRC_7331 on 3/14/2016.
 */
public class Thresholder {
    private static Thresholder ourInstance = new Thresholder();

    public static Thresholder getInstance() {
        return ourInstance;
    }


    private final HistogramCreator HISTOGRAM_CREATOR = HistogramCreator.getInstance();

    private double lastThresholdValue = 0.0;

    private Thresholder() {
    }

    public ExMat applyThreshold(ExMat sourceImg, double threshold) {
        ExMat thresholdedImg = new ExMat(ExMat.ColorType.GRAY);
        Imgproc.threshold(sourceImg, thresholdedImg, threshold, UnsignedByte.MAX_INT, Imgproc.THRESH_BINARY);

        lastThresholdValue = threshold;
        return thresholdedImg;
    }

    public ExMat autoThreshold(ExMat image) {
        ExMat thresholdedImg = new ExMat(ExMat.ColorType.GRAY);
        lastThresholdValue = Imgproc.threshold(image, thresholdedImg, 0, UnsignedByte.MAX_INT, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
//        Imgproc.adaptiveThreshold(image.getMat(), thresholdedImg.getMat(),UNSIGNED_BYTE_TOOL.getMaxInt(), Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,21,0);
        return thresholdedImg;
    }

    public ExMat autoThreshold(ExMat sourceImg, ThresholdFinder thresholdFinder) {
        byte[] data = sourceImg.getData();
        int[] histogram = HISTOGRAM_CREATOR.createHistogram(data);
        int threshold = thresholdFinder.findThreshold(histogram);
        return applyThreshold(sourceImg, threshold);
    }

    public HistogramCreator getHistogramCreator() {
        return HISTOGRAM_CREATOR;
    }

    public double getLastThresholdValue() {
        return lastThresholdValue;
    }
}
